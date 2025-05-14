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
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PillViewModel @Inject constructor(
    private val medicationUseCases: MedicationUseCases
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _currentMedications = MutableLiveData<List<Medication>>(emptyList())
    val currentMedications: LiveData<List<Medication>> = _currentMedications

    private val _pastMedications = MutableLiveData<List<Medication>>(emptyList())
    val pastMedications: LiveData<List<Medication>> = _pastMedications

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _noCurrentMedicationsVisible = MutableLiveData<Boolean>(false)
    val noCurrentMedicationsVisible: LiveData<Boolean> = _noCurrentMedicationsVisible

    private val _noPastMedicationsVisible = MutableLiveData<Boolean>(false)
    val noPastMedicationsVisible: LiveData<Boolean> = _noPastMedicationsVisible

    private var allMedications: List<Medication> = emptyList()
    private var currentSearchQuery: String = ""
    private var lastChar: Char? = null

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
            Timber.d("Loading medications")

            when (val result = medicationUseCases.getMedications()) {
                is Result.Success -> {
                    allMedications = result.data
                    Timber.d("Loaded ${allMedications.size} medications from source")
                    if (currentSearchQuery.isNotEmpty()) {
                        Timber.d("Applying existing filter: '$currentSearchQuery'")
                        onSearchQueryChanged(currentSearchQuery)
                    } else {
                        updateMedicationLists(allMedications)
                    }
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
        Timber.d("Search query changed: '$query'")
        currentSearchQuery = query

        val currentChar = query.lastOrNull()
        if (currentChar != null && currentChar == lastChar) {
            Timber.d("Duplicate character detected: '$currentChar'. Resetting list.")
        }
        lastChar = currentChar

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val filteredMedications = if (query.isEmpty()) {
                    Timber.d("Empty query - showing all medications")
                    allMedications
                } else {
                    Timber.d("Filtering medications by query: '$query'")
                    allMedications.filter { medication ->
                        medication.name.contains(query, ignoreCase = true) ||
                                medication.notes.contains(query, ignoreCase = true)
                    }
                }
                Timber.d("Filter result: ${filteredMedications.size} medications")
                updateMedicationLists(filteredMedications)
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateMedicationLists(medications: List<Medication>) {
        val today = LocalDate.now()

        val current = medications.filter { medication ->
            !today.isBefore(medication.startDate) && !today.isAfter(medication.endDate)
        }.sortedWith(
            compareByDescending<Medication> { it.startDate }.thenBy { it.name }
        )

        val past = medications.filter { medication ->
            today.isAfter(medication.endDate)
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

            when (val result = medicationUseCases.deleteMedication(medicationId)) {
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