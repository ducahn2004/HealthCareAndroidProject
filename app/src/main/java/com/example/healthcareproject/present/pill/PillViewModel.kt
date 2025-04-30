package com.example.healthcareproject.present.pill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.medication.MedicationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PillViewModel @Inject constructor(
    private val medicationUseCases: MedicationUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow(PillUiState())
    val uiState: StateFlow<PillUiState> = _uiState.asStateFlow()

    init {
        loadMedications()
    }

    fun navigateToAddMedication() {
        // Handled in PillFragment via MainNavigator
    }

    fun loadMedications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = medicationUseCases.getMedications()
            _uiState.update { state ->
                when (result) {
                    is Result.Success -> {
                        val (current, past) = result.data.partition { medication ->
                            val today = LocalDate.now()
                            val endDate = medication.endDate ?: LocalDate.now().plusYears(1)
                            today in medication.startDate..endDate
                        }
                        state.copy(
                            currentMedications = current,
                            pastMedications = past,
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

    fun addMedication(medication: Medication) {
        viewModelScope.launch {
            val result = medicationUseCases.createMedication(
                name = medication.name,
                dosageUnit = medication.dosageUnit.toString(),
                dosageAmount = medication.dosageAmount,
                frequency = medication.frequency,
                timeOfDay = medication.timeOfDay,
                mealRelation = medication.mealRelation.toString(),
                startDate = medication.startDate,
                endDate = medication.endDate,
                notes = medication.notes
            )
            when (result) {
                is Result.Success -> loadMedications()
                is Result.Error -> _uiState.update {
                    it.copy(error = result.exception.message)
                }
                is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }
}

data class PillUiState(
    val currentMedications: List<Medication> = emptyList(),
    val pastMedications: List<Medication> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)