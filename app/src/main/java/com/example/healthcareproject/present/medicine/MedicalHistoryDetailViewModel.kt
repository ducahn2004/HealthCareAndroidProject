package com.example.healthcareproject.present.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.usecase.medicalvisit.MedicalVisitUseCases
import com.example.healthcareproject.domain.usecase.medication.MedicationUseCases
import com.example.healthcareproject.present.medicine.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class MedicalHistoryDetailViewModel @Inject constructor(
    private val medicalVisitUseCases: MedicalVisitUseCases,
    private val medicationUseCases: MedicationUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    fun loadDetails(visitId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val medicalVisit = medicalVisitUseCases.getMedicalVisitUseCase(visitId)
                val medications = medicationUseCases.getMedicationsByVisitId(visitId)

                _uiState.update { state ->
                    if (medicalVisit != null) {
                        state.copy(
                            medicalVisit = medicalVisit,
                            medications = medications,
                            isLoading = false,
                            error = null,
                            formattedDate = medicalVisit.visitDate.format(dateFormatter),
                            formattedTime = medicalVisit.createdAt.format(timeFormatter)
                        )
                    } else {
                        state.copy(
                            isLoading = false,
                            error = "Medical visit not found",
                            medications = emptyList()
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e.message ?: "An error occurred"
                    )
                }
            }
        }
    }
}