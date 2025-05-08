package com.example.healthcareproject.present.viewmodel.medication

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.medication.MedicationUseCases
import com.example.healthcareproject.domain.usecase.medicalvisit.MedicalVisitUseCases
import com.example.healthcareproject.present.ui.toLocalDate
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
    private val medicationUseCases: MedicationUseCases,
    private val medicalVisitUseCases: MedicalVisitUseCases,
    private val authDataSource: AuthDataSource
) : ViewModel() {
    // Observable fields for two-way data binding with the layout
    val medicationName = ObservableField<String>("")
    val dosageAmount = ObservableField<String>("")
    val dosageUnit = ObservableField<DosageUnit>(DosageUnit.None)
    val frequency = ObservableField<String>("")
    val timeOfDay = ObservableField<String>("")
    val mealRelation = ObservableField<MealRelation>(MealRelation.None)
    val startDate = ObservableField<LocalDate>()
    val endDate = ObservableField<LocalDate>()
    val notes = ObservableField<String>("")

    // Formatted date fields for TextView binding
    val formattedStartDate = ObservableField<String>("")
    val formattedEndDate = ObservableField<String>("Select End Date (optional)")

    // Observable for loading state
    val isLoading = ObservableField<Boolean>(false)

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
    }

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

    fun addMedication() {
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

        val userId = authDataSource.getCurrentUserId() ?: run {
            _error.value = "User not logged in"
            return
        }

        val medicationStartDate = startDate.get() ?: LocalDate.now()
        val medication = Medication(
            medicationId = UUID.randomUUID().toString(),
            name = medicationName.get() ?: "",
            dosageAmount = dosageAmount.get()?.toFloatOrNull() ?: 0f,
            dosageUnit = dosageUnit.get() ?: DosageUnit.None,
            frequency = frequency.get()?.toIntOrNull() ?: 1,
            timeOfDay = timeOfDayList,
            mealRelation = mealRelation.get() ?: MealRelation.None,
            startDate = medicationStartDate,
            endDate = endDate.get() ?: medicationStartDate,
            notes = notes.get() ?: "",
            userId = userId,
            visitId = visitId ?: ""
        )

        // Save medication directly via use case
        viewModelScope.launch {
            isLoading.set(true)
            val result = medicationUseCases.createMedication(
                visitId = visitId,
                name = medication.name,
                dosageUnit = medication.dosageUnit,
                dosageAmount = medication.dosageAmount,
                frequency = medication.frequency,
                timeOfDay = medication.timeOfDay,
                mealRelation = medication.mealRelation,
                startDate = medication.startDate,
                endDate = medication.endDate,
                notes = medication.notes
            )
            isLoading.set(false)

            when (result) {
                is Result.Success -> {
                    Timber.d("Medication saved: ${medication.name} with ID: ${result.data}")
                    // Clear input fields
//                    medicationName.set("")
//                    dosageAmount.set("")
//                    dosageUnit.set(DosageUnit.None)
//                    frequency.set("")
//                    timeOfDay.set("")
//                    mealRelation.set(MealRelation.None)
//                    startDate.set(null)
//                    endDate.set(null)
//                    notes.set("")
                    _error.value = null
                    _isFinished.value = true
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Failed to save medication: ${medication.name}")
                    _error.value = result.exception.message ?: "Failed to save medication"
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun updateMedication() {
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

        viewModelScope.launch {
            isLoading.set(true)
            val result = medicationUseCases.updateMedication(
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