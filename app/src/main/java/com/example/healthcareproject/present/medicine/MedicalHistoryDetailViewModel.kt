package com.example.healthcareproject.present.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.medicalvisit.MedicalVisitUseCases
import com.example.healthcareproject.domain.usecase.medication.MedicationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicalHistoryDetailViewModel @Inject constructor(
    private val medicalVisitUseCases: MedicalVisitUseCases,
    private val medicationUseCases: MedicationUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadDetails(visitId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            medicalVisitUseCases.getMedicalVisitUseCase(visitId).collect { visitResult ->
                medicationUseCases.getMedicationsByVisitId(visitId).collect { medicationResult ->
                    _uiState.update { state ->
                        when (visitResult) {
                            is Result.Success -> {
                                when (medicationResult) {
                                    is Result.Success -> state.copy(
                                        medicalVisit = visitResult.data,
                                        medications = medicationResult.data,
                                        isLoading = false,
                                        error = null
                                    )
                                    is Result.Error -> state.copy(
                                        isLoading = false,
                                        error = medicationResult.exception.message
                                    )
                                    is Result.Loading -> state.copy(isLoading = true)
                                }
                            }
                            is Result.Error -> state.copy(
                                isLoading = false,
                                error = visitResult.exception.message
                            )
                            is Result.Loading -> state.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }
}

data class DetailUiState(
    val medicalVisit: MedicalVisit? = null,
    val medications: List<Medication> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)