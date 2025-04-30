package com.example.healthcareproject.present.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.medicalvisit.MedicalVisitUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val medicalVisitUseCases: MedicalVisitUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow(MedicineUiState())
    val uiState: StateFlow<MedicineUiState> = _uiState.asStateFlow()

    init {
        loadMedicalVisits()
    }

    fun loadMedicalVisits() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            medicalVisitUseCases.getMedicalVisitsUseCase().collect { result ->
                _uiState.update { state ->
                    when (result) {
                        is Result.Success -> {
                            val (before, after) = result.data.partition { visit ->
                                visit.visitDate.isBefore(LocalDate.now())
                            }
                            state.copy(
                                visitsBefore = before,
                                visitsAfter = after,
                                isLoading = false,
                                error = null
                            )
                        }
                        is Result.Error -> state.copy(
                            isLoading = false,
                            error = result.exception.message
                        )
                        is Result.Loading -> state.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filteredBefore = if (query.isBlank()) {
                state.visitsBefore
            } else {
                state.visitsBefore.filter {
                    it.diagnosis.contains(query, ignoreCase = true) ||
                            it.doctorName.contains(query, ignoreCase = true) ||
                            it.clinicName.contains(query, ignoreCase = true)
                }
            }
            val filteredAfter = if (query.isBlank()) {
                state.visitsAfter
            } else {
                state.visitsAfter.filter {
                    it.diagnosis.contains(query, ignoreCase = true) ||
                            it.doctorName.contains(query, ignoreCase = true) ||
                            it.clinicName.contains(query, ignoreCase = true)
                }
            }
            state.copy(
                visitsBefore = filteredBefore,
                visitsAfter = filteredAfter
            )
        }
    }
}

data class MedicineUiState(
    val visitsBefore: List<MedicalVisit> = emptyList(),
    val visitsAfter: List<MedicalVisit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)