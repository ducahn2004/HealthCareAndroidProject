package com.example.healthcareproject.presentation.viewmodel.medication

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.medication.DeleteMedicationUseCase
import com.example.healthcareproject.domain.usecase.medication.GetMedicationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PillViewModel @Inject constructor(
    private val getMedicationsUseCase: GetMedicationsUseCase,
    private val deleteMedicationUseCase: DeleteMedicationUseCase
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _currentMedications = MutableLiveData<List<Medication>>(emptyList())
    val currentMedications: LiveData<List<Medication>> = _currentMedications

    private val _pastMedications = MutableLiveData<List<Medication>>(emptyList())
    val pastMedications: LiveData<List<Medication>> = _pastMedications

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _noCurrentMedicationsVisible = MutableLiveData(false)
    val noCurrentMedicationsVisible: LiveData<Boolean> = _noCurrentMedicationsVisible

    private val _noPastMedicationsVisible = MutableLiveData(false)
    val noPastMedicationsVisible: LiveData<Boolean> = _noPastMedicationsVisible

    private val _searchEvent = MutableSharedFlow<String>()
    val searchEvent: SharedFlow<String> = _searchEvent

    private var allMedications: List<Medication> = emptyList()
    private var currentSearchQuery: String = ""

    val noCurrentMedicationsVisibility: Int
        get() = if (_noCurrentMedicationsVisible.value == true) View.VISIBLE else View.GONE

    val noPastMedicationsVisibility: Int
        get() = if (_noPastMedicationsVisible.value == true) View.VISIBLE else View.GONE

    init {
        loadMedications()
    }

    private fun loadMedications() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            Timber.d("Loading medications")

            when (val result = getMedicationsUseCase()) {
                is Result.Success -> {
                    allMedications = result.data
                    Timber.d("Loaded ${allMedications.size} medications from source")
                    filterAndUpdateMedications(currentSearchQuery)
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.exception.message ?: "Failed to load medications"
                    _isLoading.value = false
                    Timber.e(result.exception, "Error loading medications")
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        currentSearchQuery = query
        viewModelScope.launch {
            try {
                filterAndUpdateMedications(query)
                _searchEvent.emit(query)
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
                Timber.e(e, "Unexpected error during search")
            }
        }
    }

    private fun filterAndUpdateMedications(query: String) {
        viewModelScope.launch {
            try {
                val filteredMedications = if (query.isEmpty()) {
                    allMedications
                } else {
                    allMedications.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.notes.contains(query, ignoreCase = true) ||
                                it.dosageUnit.name.contains(query, ignoreCase = true) ||
                                it.dosageAmount.toString().contains(query)
                    }
                }
                Timber.d("Filtered ${filteredMedications.size} medications for query: '$query'")
                updateMedicationLists(filteredMedications.toList()) // New list
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
                Timber.e(e, "Unexpected error during search")
            }
        }
    }

    private fun updateMedicationLists(medications: List<Medication>) {
        val today = LocalDate.now()

        val current = medications.filter { medication ->
            val endDate = medication.endDate
            !today.isBefore(medication.startDate) && !today.isAfter(endDate)
        }.sortedWith(
            compareByDescending<Medication> { it.startDate }.thenBy { it.name }
        )

        val past = medications.filter { medication ->
            val endDate = medication.endDate
            today.isAfter(endDate)
        }.sortedWith(
            compareByDescending<Medication> { it.endDate }.thenBy { it.name }
        )

        Timber.d("Current medications: ${current.size}, Past medications: ${past.size}")

        _currentMedications.value = current
        _pastMedications.value = past
        _noCurrentMedicationsVisible.value = current.isEmpty()
        _noPastMedicationsVisible.value = past.isEmpty()
    }

    fun deleteMedication(medicationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = deleteMedicationUseCase(medicationId)) {
                is Result.Success<*> -> {
                    loadMedications()
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

    fun clearError() {
        _error.value = null
    }
}