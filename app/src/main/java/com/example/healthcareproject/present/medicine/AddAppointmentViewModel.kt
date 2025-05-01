package com.example.healthcareproject.present.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.appointment.AppointmentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject



@HiltViewModel
class AddAppointmentViewModel @Inject constructor(
    private val appointmentUseCases: AppointmentUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppointmentUiState())
    val uiState: StateFlow<AppointmentUiState> = _uiState.asStateFlow()

    fun updateDiagnosis(diagnosis: String) {
        _uiState.update { it.copy(diagnosis = diagnosis, diagnosisError = null) }
    }

    fun updateDoctorName(doctorName: String) {
        _uiState.update { it.copy(doctorName = doctorName, doctorNameError = null) }
    }

    fun updateClinicName(clinicName: String) {
        _uiState.update { it.copy(clinicName = clinicName, clinicNameError = null) }
    }

    fun updateTreatment(treatment: String) {
        _uiState.update { it.copy(treatment = treatment, treatmentError = null) }
    }

    fun updateVisitDate(visitDate: LocalDate) {
        _uiState.update { it.copy(visitDate = visitDate, dateError = null) }
    }

    fun updateTime(time: LocalTime) {
        _uiState.update { it.copy(time = time, timeError = null) }
    }

    fun saveAppointment() {
        val state = _uiState.value
        var hasError = false

        // Validation
        if (state.diagnosis.isBlank()) {
            _uiState.update { it.copy(diagnosisError = "Required") }
            hasError = true
        }
        if (state.doctorName.isBlank()) {
            _uiState.update { it.copy(doctorNameError = "Required") }
            hasError = true
        }
        if (state.clinicName.isBlank()) {
            _uiState.update { it.copy(clinicNameError = "Required") }
            hasError = true
        }
        if (state.treatment.isBlank()) {
            _uiState.update { it.copy(treatmentError = "Required") }
            hasError = true
        }
        if (state.visitDate == null) {
            _uiState.update { it.copy(dateError = "Required") }
            hasError = true
        }
        if (state.time == null) {
            _uiState.update { it.copy(timeError = "Required") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Combine date and time
            val createdAt = LocalDateTime.of(state.visitDate, state.time)

            val medicalVisit = MedicalVisit(
                visitId = "", // Assume repository generates
                userId = "",  // Assume repository sets
                visitDate = state.visitDate!!,
                clinicName = state.clinicName,
                doctorName = state.doctorName,
                diagnosis = state.diagnosis,
                treatment = state.treatment,
                createdAt = createdAt
            )

            // Call use case (adjust based on your actual use case)
            val result = appointmentUseCases.createAppointment(
                doctorName = state.doctorName,
                location = "", // Not in MedicalVisit, pass empty
                appointmentTime = createdAt,
                note = state.diagnosis
            )

            _uiState.update { state ->
                when (result) {
                    is Result.Success -> state.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                    is Result.Error -> state.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = result.exception.message
                    )
                    is Result.Loading -> state.copy(isLoading = true)
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}