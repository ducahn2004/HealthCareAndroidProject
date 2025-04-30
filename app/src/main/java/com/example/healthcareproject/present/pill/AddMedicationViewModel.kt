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
        location: String?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)
            val patientName = authDataSource.getCurrentUserId() ?: "Unknown Patient"
            val visitResult = medicalVisitUseCases.createMedicalVisitUseCase(
                patientName = patientName,
                visitReason = clinicName, // Map clinicName to visitReason
                visitDate = _uiState.value?.visitDate ?: LocalDate.now(),
                doctorName = doctorName,
                diagnosis = diagnosis,
                treatment = location,
                status = true
            )
            when (visitResult) {
                is Result.Success -> {
                    _uiState.value = _uiState.value?.copy(
                        visitId = visitResult.data,
                        isVisitSaved = true,
                        isLoading = false,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        error = visitResult.exception.message
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun saveMedicationAndFinish(medication: Medication) {
        viewModelScope.launch {
            val visitId = _uiState.value?.visitId ?: return@launch
            val userId = authDataSource.getCurrentUserId() ?: return@launch

            _uiState.value = _uiState.value?.copy(isLoading = true)

            // Create a complete medication with visitId
            val completemedication = medication.copy(
                medicationId = UUID.randomUUID().toString(),  // Generate a new ID
                userId = userId,
                visitId = visitId                            // Link to the current visit
            )

            val medicationResult = medicationUseCases.createMedication(
                name = completemedication.name,
                dosageUnit = completemedication.dosageUnit.toString(),
                dosageAmount = completemedication.dosageAmount.toFloat(),
                frequency = completemedication.frequency,
                timeOfDay = completemedication.timeOfDay,
                mealRelation = completemedication.mealRelation?.toString(),
                startDate = completemedication.startDate,
                endDate = completemedication.endDate ?: completemedication.startDate.plusMonths(1),
                notes = completemedication.notes ?: ""
            )

            when (medicationResult) {
                is Result.Success -> {
                    _uiState.value = _uiState.value?.copy(
                        medication = completemedication,  // Store the created medication in UI state
                        isLoading = false,
                        isFinished = true,                // Mark process as finished
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        error = medicationResult.exception.message
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }
}

data class AddMedicationUiState(
    val visitDate: LocalDate? = null,
    val visitTime: Calendar? = null,
    val visitId: String? = null,
    val isVisitSaved: Boolean = false,
    val medication: Medication? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFinished: Boolean = false
)