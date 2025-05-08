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
    val dosageUnit = ObservableField<DosageUnit>(DosageUnit.None) // Default to MG
    val frequency = ObservableField<String>("")
    val timeOfDay = ObservableField<String>("") // Comma-separated times, e.g., "08:00,14:00"
    val mealRelation = ObservableField<MealRelation>(MealRelation.None) // Default
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
    private val medications = mutableListOf<Medication>()

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

    fun setDosageUnit(unit: DosageUnit) {
        dosageUnit.set(unit)
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

    fun saveMedicalVisit(
        diagnosis: String,
        doctorName: String,
        clinicName: String
    ) {
        viewModelScope.launch {
            isLoading.set(true)
            val patientName = authDataSource.getCurrentUserId() ?: run {
                isLoading.set(false)
                _error.value = "User not logged in"
                return@launch
            }
            val visitResult = medicalVisitUseCases.createMedicalVisitUseCase(
                patientName = patientName,
                visitReason = clinicName,
                visitDate = visitDate ?: LocalDate.now(),
                doctorName = doctorName,
                diagnosis = diagnosis,
                status = true
            )
            when (visitResult) {
                is Result.Success -> {
                    Timber.d("MedicalVisit saved with visitId: ${visitResult.data}")
                    visitId = visitResult.data
                    isLoading.set(false)
                    _error.value = null
                }
                is Result.Error -> {
                    Timber.e(visitResult.exception, "Failed to save MedicalVisit")
                    isLoading.set(false)
                    _error.value = visitResult.exception.message ?: "Failed to save appointment"
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun addMedication() {
        val timeOfDayList = timeOfDay.get()?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
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
            endDate = endDate.get() ?: medicationStartDate, // Fixed: Use startDate as fallback for non-null endDate
            notes = notes.get() ?: "",
            userId = "",
            visitId = ""
        )
        medications.add(medication)

        // Clear input fields after adding
        medicationName.set("")
        dosageAmount.set("")
        dosageUnit.set(DosageUnit.None)
        frequency.set("")
        timeOfDay.set("")
        mealRelation.set(MealRelation.None)
        startDate.set(null)
        endDate.set(null)
        notes.set("")
    }

    fun saveAllMedications() {
        viewModelScope.launch {
            val currentVisitId = visitId ?: run {
                isLoading.set(false)
                _error.value = "No visit ID available"
                return@launch
            }
            val userId = authDataSource.getCurrentUserId() ?: run {
                isLoading.set(false)
                _error.value = "User not logged in"
                return@launch
            }
            if (medications.isEmpty()) {
                isLoading.set(false)
                _error.value = "No medications to save"
                return@launch
            }

            isLoading.set(true)

            medications.forEach { medication ->
                val medicationResult = medicationUseCases.createMedication(
                    visitId = currentVisitId,
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
                when (medicationResult) {
                    is Result.Success -> Timber.d("Medication saved: ${medication.name} with ID: ${medicationResult.data}")
                    is Result.Error -> {
                        Timber.e(medicationResult.exception, "Failed to save medication: ${medication.name}")
                        isLoading.set(false)
                        _error.value = medicationResult.exception.message ?: "Failed to save medication"
                        return@launch
                    }
                    is Result.Loading -> Unit
                }
            }

            isLoading.set(false)
            _isFinished.value = true
            _error.value = null
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
}