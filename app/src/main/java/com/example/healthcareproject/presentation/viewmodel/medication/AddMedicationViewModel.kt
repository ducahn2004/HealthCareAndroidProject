package com.example.healthcareproject.presentation.viewmodel.medication

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.medication.CreateMedicationUseCase
import com.example.healthcareproject.domain.usecase.medication.UpdateMedicationUseCase
import com.example.healthcareproject.presentation.util.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddMedicationViewModel @Inject constructor(
    private val createMedicationUseCase: CreateMedicationUseCase,
    private val updateMedicationUseCase: UpdateMedicationUseCase,
    private val authDataSource: AuthDataSource
) : ViewModel() {
    // Observable fields for two-way data binding with the layout
    val medicationName = ObservableField("")
    val dosageAmount = ObservableField("")
    val dosageUnit = ObservableField(DosageUnit.None)
    val frequency = ObservableField("")
    val timeOfDay = ObservableField("")
    val mealRelation = ObservableField(MealRelation.None)
    val startDate = ObservableField<LocalDate>()
    val endDate = ObservableField<LocalDate>()
    val notes = ObservableField("")

    // Formatted date fields for TextView binding
    val formattedStartDate = ObservableField("")
    val formattedEndDate = ObservableField("Select End Date (optional)")

    // Observable for loading state
    val isLoading = ObservableField(false)

    // LiveData for error messages and navigation
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isFinished = MutableLiveData<Boolean>()
    val isFinished: LiveData<Boolean> = _isFinished

    // Internal state for visit-related data
    private var visitId: String? = null
    private var visitDate: LocalDate? = null
    private var visitTime: Calendar? = null
    private var medicationId: String = ""

    init {
        // Set up observers for date changes
        startDate.addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                updateFormattedStartDate()
            }
        })
        endDate.addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                updateFormattedEndDate()
            }
        })
    }

    fun setVisitDate(calendar: Calendar) {
        visitDate = calendar.time.toLocalDate()
        startDate.set(visitDate) // Default start date to visit date
    }


    fun setVisitTime(calendar: Calendar) {
        visitTime = calendar
    }

    fun setVisitId(id: String?) {
        visitId = id
        Timber.d("ViewModel visitId set to: $visitId")
    }

    fun getVisitId(): String? = visitId

    fun setDosageUnit(unit: DosageUnit) {
        dosageUnit.set(unit)
    }

    fun setMedicationId(id: String) {
        medicationId = id
    }

    fun setMealRelation(relation: MealRelation) {
        mealRelation.set(relation)
    }

    fun setStartDate(date: LocalDate) {
        startDate.set(date)
    }

    fun setEndDate(date: LocalDate?) {
        endDate.set(date)
    }

    fun addMedication(syncToNetwork: Boolean) {
        // Validate time of day format
        val timeOfDayList = timeOfDay.get()?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
        try {
            timeOfDayList.forEach { time ->
                require(isValidTimeFormat(time)) { "Invalid time format: $time" }
            }
        } catch (e: IllegalArgumentException) {
            _error.value = e.message
            return
        }

        if (visitId != null) {
            Timber.d("Skipping direct save for MedicalVisitFragment, visitId: $visitId")
            _isFinished.value = true
            return
        }

        val userId = authDataSource.getCurrentUserId() ?: run {
            _error.value = "User not logged in"
            return
        }

        val medicationStartDate = startDate.get() ?: LocalDate.now()
        isLoading.set(true)
        viewModelScope.launch {
            try {
                val result = createMedicationUseCase(
                    visitId = visitId,
                    name = medicationName.get()
                        ?: throw IllegalArgumentException("Medication name is required"),
                    dosageUnit = dosageUnit.get()
                        ?: throw IllegalArgumentException("Dosage unit is required"),
                    dosageAmount = dosageAmount.get()?.toFloatOrNull()
                        ?: throw IllegalArgumentException("Invalid dosage amount"),
                    frequency = frequency.get()?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid frequency"),
                    timeOfDay = timeOfDay.get()?.split(",")?.map { it.trim() }
                        ?.filter { it.isNotEmpty() }
                        ?: throw IllegalArgumentException("Time of day is required"),
                    mealRelation = mealRelation.get()
                        ?: throw IllegalArgumentException("Meal relation is required"),
                    startDate = startDate.get()
                        ?: throw IllegalArgumentException("Start date is required"),
                    endDate = endDate.get() ?: LocalDate.now().plusMonths(1),
                    notes = notes.get() ?: "",
                    syncToNetwork = syncToNetwork
                )
                when (result) {
                    is Result.Success -> {
                        _isFinished.value = true
                        _error.value = null
                    }
                    is Result.Error -> {
                        _error.value = result.exception.message
                    }
                    else -> {
                        Timber.d("Unexpected result state: $result")
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to add medication: ${e.message}"
            } finally {
                isLoading.set(false)
            }
        }
    }

    fun updateMedication() {
        Timber.d("updateMedication called with visitId: $visitId, medicationId: $medicationId")
        if (medicationId.isBlank()) {
            _error.value = "Medication ID cannot be empty"
            return
        }

        val timeOfDayList = timeOfDay.get()?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
        try {
            timeOfDayList.forEach { time ->
                require(isValidTimeFormat(time)) { "Invalid time format: $time" }
            }
        } catch (e: IllegalArgumentException) {
            _error.value = e.message
            return
        }

        if (visitId != null) {
            Timber.d("Skipping direct save for MedicalVisitFragment, visitId: $visitId")
            _isFinished.value = true
            return
        }

        viewModelScope.launch {
            isLoading.set(true)
            val result = updateMedicationUseCase(
                medicationId = medicationId,
                name = medicationName.get() ?: "",
                dosageUnit = dosageUnit.get() ?: DosageUnit.None,
                dosageAmount = dosageAmount.get()?.toFloatOrNull() ?: 0f,
                frequency = frequency.get()?.toIntOrNull() ?: 1,
                timeOfDay = timeOfDayList,
                mealRelation = mealRelation.get() ?: MealRelation.None,
                startDate = startDate.get() ?: LocalDate.now(),
                endDate = endDate.get() ?: startDate.get() ?: LocalDate.now(),
                notes = notes.get() ?: ""
            )
            isLoading.set(false)

            when (result) {
                is Result.Success -> {
                    Timber.d("Medication updated: ${medicationName.get()} with ID: $medicationId")
                    medicationName.set("")
                    dosageAmount.set("")
                    dosageUnit.set(DosageUnit.None)
                    frequency.set("")
                    timeOfDay.set("")
                    mealRelation.set(MealRelation.None)
                    startDate.set(null)
                    endDate.set(null)
                    notes.set("")
                    medicationId = ""
                    _error.value = null
                    _isFinished.value = true
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Failed to update medication: ${medicationName.get()}")
                    _error.value = result.exception.message ?: "Failed to update medication"
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun updateFormattedStartDate() {
        val date = startDate.get()
        formattedStartDate.set(date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "")
    }

    private fun updateFormattedEndDate() {
        val date = endDate.get()
        formattedEndDate.set(date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Select End Date (optional)")
    }

    private fun isValidTimeFormat(time: String): Boolean {
        return try {
            LocalTime.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }
}