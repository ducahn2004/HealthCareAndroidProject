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

    // Cache for unfiltered lists
    private var allVisitsBefore: List<MedicalVisit> = emptyList()
    private var allVisitsAfter: List<MedicalVisit> = emptyList()

    init {
        loadMedicalVisits()
    }

    fun loadMedicalVisits() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Get visits from use case
                val visits = medicalVisitUseCases.getMedicalVisitsUseCase()

                // Partition visits into before and after current date
                val (before, after) = visits.partition { visit ->
                    visit.visitDate.isBefore(LocalDate.now())
                }

                // Store in cache
                allVisitsBefore = before
                allVisitsAfter = after

                _uiState.update { state ->
                    state.copy(
                        visitsBefore = before,
                        visitsAfter = after,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            val filteredBefore = if (query.isBlank()) {
                allVisitsBefore
            } else {
                allVisitsBefore.filter { visit ->
                    visit.diagnosis.contains(query, ignoreCase = true) ||
                            visit.doctorName.contains(query, ignoreCase = true) ||
                            visit.clinicName.contains(query, ignoreCase = true)
                }
            }

            val filteredAfter = if (query.isBlank()) {
                allVisitsAfter
            } else {
                allVisitsAfter.filter { visit ->
                    visit.diagnosis.contains(query, ignoreCase = true) ||
                            visit.doctorName.contains(query, ignoreCase = true) ||
                            visit.clinicName.contains(query, ignoreCase = true)
                }
            }

            _uiState.update { state ->
                state.copy(
                    visitsBefore = filteredBefore,
                    visitsAfter = filteredAfter
                )
            }
        }
    }
}