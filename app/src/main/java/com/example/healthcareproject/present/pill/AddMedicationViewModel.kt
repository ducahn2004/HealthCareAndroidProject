package com.example.healthcareproject.present.pill

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
import com.example.healthcareproject.present.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddMedicationViewModel @Inject constructor(
    private val medicationUseCases: MedicationUseCases,
    private val medicalVisitUseCases: MedicalVisitUseCases,
    private val authDataSource: AuthDataSource
) : ViewModel() {
    private val _uiState = MutableLiveData<AddMedicationUiState>()
    val uiState: LiveData<AddMedicationUiState> = _uiState

    init {
        _uiState.value = AddMedicationUiState()
    }

    fun setVisitDate(calendar: Calendar) {
        _uiState.value = _uiState.value?.copy(visitDate = calendar.time.toLocalDate())
    }

    fun setVisitTime(calendar: Calendar) {
        _uiState.value = _uiState.value?.copy(visitTime = calendar)
    }

    fun saveMedicalVisit(
        diagnosis: String,
        doctorName: String,
        clinicName: String,
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)
            val patientName = authDataSource.getCurrentUserId() ?: run {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    error = "User not logged in"
                )
                return@launch
            }
            val visitResult = medicalVisitUseCases.createMedicalVisitUseCase(
                patientName = patientName,
                visitReason = clinicName,
                visitDate = _uiState.value?.visitDate ?: LocalDate.now(),
                doctorName = doctorName,
                diagnosis = diagnosis,
                status = true
            )
            when (visitResult) {
                is Result.Success -> {
                    Timber.d("MedicalVisit saved with visitId: ${visitResult.data}")
                    _uiState.value = _uiState.value?.copy(
                        visitId = visitResult.data,
                        isVisitSaved = true,
                        isLoading = false,
                        error = null
                    )
                }
                is Result.Error -> {
                    Timber.e(visitResult.exception, "Failed to save MedicalVisit")
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        error = visitResult.exception.message ?: "Failed to save appointment"
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun addMedicationToList(medication: Medication) {
        val currentList = _uiState.value?.medications?.toMutableList() ?: mutableListOf()
        currentList.add(medication.copy(medicationId = UUID.randomUUID().toString()))
        _uiState.value = _uiState.value?.copy(medications = currentList)
    }

    fun saveAllMedications() {
        viewModelScope.launch {
            val visitId = _uiState.value?.visitId ?: run {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    error = "No visit ID available"
                )
                return@launch
            }
            val userId = authDataSource.getCurrentUserId() ?: run {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    error = "User not logged in"
                )
                return@launch
            }
            val medications = _uiState.value?.medications ?: run {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    error = "No medications to save"
                )
                return@launch
            }

            _uiState.value = _uiState.value?.copy(isLoading = true)

            medications.forEach { medication ->
                val completeMedication = medication.copy(
                    userId = userId,
                    visitId = visitId
                )
                val medicationResult = medicationUseCases.createMedication(
                    visitId = visitId,
                    name = completeMedication.name,
                    dosageUnit = completeMedication.dosageUnit,
                    dosageAmount = completeMedication.dosageAmount,
                    frequency = completeMedication.frequency,
                    timeOfDay = completeMedication.timeOfDay,
                    mealRelation = completeMedication.mealRelation,
                    startDate = completeMedication.startDate,
                    endDate = completeMedication.endDate,
                    notes = completeMedication.notes ?: ""
                )
                when (medicationResult) {
                    is Result.Success<String> -> Timber.d("Medication saved: ${completeMedication.name}")
                    is Result.Error -> {
                        Timber.e(medicationResult.exception, "Failed to save medication: ${completeMedication.name}")
                        _uiState.value = _uiState.value?.copy(
                            isLoading = false,
                            error = medicationResult.exception.message ?: "Failed to save medication"
                        )
                        return@launch
                    }
                    is Result.Loading -> Unit
                }
            }

            _uiState.value = _uiState.value?.copy(
                isLoading = false,
                isFinished = true,
                error = null
            )
        }
    }
}

data class AddMedicationUiState(
    val visitDate: LocalDate? = null,
    val visitTime: Calendar? = null,
    val visitId: String? = null,
    val isVisitSaved: Boolean = false,
    val medications: List<Medication> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFinished: Boolean = false
)