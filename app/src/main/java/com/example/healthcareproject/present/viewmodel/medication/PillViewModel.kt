package com.example.healthcareproject.present.viewmodel.medication

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.medication.MedicationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PillViewModel @Inject constructor(
    private val medicationUseCases: MedicationUseCases
) : ViewModel() {
    // LiveData for directly binding to views
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _currentMedications = MutableLiveData<List<Medication>>(emptyList())
    val currentMedications: LiveData<List<Medication>> = _currentMedications

    private val _pastMedications = MutableLiveData<List<Medication>>(emptyList())
    val pastMedications: LiveData<List<Medication>> = _pastMedications

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // Visibility helpers for data binding
    private val _noCurrentMedicationsVisible = MutableLiveData<Boolean>(false)
    val noCurrentMedicationsVisible: LiveData<Boolean> = _noCurrentMedicationsVisible

    private val _noPastMedicationsVisible = MutableLiveData<Boolean>(false)
    val noPastMedicationsVisible: LiveData<Boolean> = _noPastMedicationsVisible

    // Convenience properties for direct binding in layout
    val loadingVisibility: Int
        get() = if (_isLoading.value == true) View.VISIBLE else View.GONE

    val noCurrentMedicationsVisibility: Int
        get() = if (_noCurrentMedicationsVisible.value == true) View.VISIBLE else View.GONE

    val noPastMedicationsVisibility: Int
        get() = if (_noPastMedicationsVisible.value == true) View.VISIBLE else View.GONE

    init {
        loadMedications()
    }

    fun loadMedications() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = medicationUseCases.getMedications()

            when (result) {
                is Result.Success -> {
                    val (current, past) = result.data.partition { medication ->
                        val today = LocalDate.now()
                        val endDate = medication.endDate ?: LocalDate.now().plusYears(1)
                        today in medication.startDate..endDate
                    }

                    val sortedCurrent = current.sortedWith(
                        compareByDescending<Medication> { it.startDate }
                            .thenBy { it.name }
                    )

                    val sortedPast = past.sortedWith(
                        compareByDescending<Medication> { it.endDate }
                            .thenBy { it.name }
                    )

                    _currentMedications.value = sortedCurrent
                    _pastMedications.value = sortedPast

                    // Update empty state indicators
                    _noCurrentMedicationsVisible.value = sortedCurrent.isEmpty()
                    _noPastMedicationsVisible.value = sortedPast.isEmpty()

                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun deleteMedication(medicationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = medicationUseCases.deleteMedication(medicationId)

            when (result) {
                is Result.Success<*> -> {
                    loadMedications() // Refresh the list after deletion
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.exception.message ?: "Failed to delete medication"
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

}