package com.example.healthcareproject.present.viewmodel.medicine

import android.util.Log
import androidx.lifecycle.*
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.repository.MedicationRepository
import com.example.healthcareproject.domain.usecase.medicalvisit.MedicalVisitUseCases
import com.example.healthcareproject.domain.usecase.medication.MedicationUseCases
import com.example.healthcareproject.domain.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MedicalHistoryDetailViewModel @Inject constructor(
    private val medicalVisitUseCases: MedicalVisitUseCases,
    private val medicationUseCases: MedicationUseCases,
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    private val _medicalVisit = MutableLiveData<MedicalVisit?>()
    val medicalVisit: LiveData<MedicalVisit?> get() = _medicalVisit

    private val _medications = MutableLiveData<List<Medication>>(emptyList())
    val medications: LiveData<List<Medication>> get() = _medications

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> get() = _deleteResult

    private val _formattedDate = MutableLiveData("")
    val formattedDate: LiveData<String> get() = _formattedDate

    private val _formattedTime = MutableLiveData("")
    val formattedTime: LiveData<String> get() = _formattedTime

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    fun loadDetails(visitId: String) {
        Timber.tag("MedicalHistoryDetail").d("Loading for visitId: $visitId")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val medicalVisit = medicalVisitUseCases.getMedicalVisitUseCase(visitId)
                val medicationsList = medicationUseCases.getMedicationsByVisitId(visitId)
                Timber.tag("MedicalHistoryDetail")
                    .d("Visit: $medicalVisit, Medications: $medicationsList")
                if (medicalVisit != null) {
                    _medicalVisit.value = medicalVisit
                    _medications.value = medicationsList
                    _formattedDate.value = medicalVisit.visitDate.format(dateFormatter)
                    _formattedTime.value = medicalVisit.createdAt.format(timeFormatter)
                    _error.value = null
                } else {
                    handleError("Medical visit not found")
                }
            } catch (e: Exception) {
                Timber.tag("MedicalHistoryDetail").e("Error: ${e.message}")
                handleError(e.message ?: "An error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMedication(medicationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = medicationUseCases.deleteMedication(medicationId)

            when (result) {
                is Result.Success -> {
                    Timber.d("Medication $medicationId deleted successfully")
                    // Cập nhật danh sách medications trực tiếp
                    _medications.value = _medications.value?.filter { it.medicationId != medicationId } ?: emptyList()
                    _deleteResult.value = result
                    _isLoading.value = false
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Failed to delete medication $medicationId")
                    _error.value = result.exception.message ?: "Failed to delete medication"
                    _deleteResult.value = result
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }
    fun updateMedication(updatedMedication: Medication) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _medications.value = _medications.value?.map { medication ->
                    if (medication.medicationId == updatedMedication.medicationId) {
                        updatedMedication
                    } else {
                        medication
                    }
                } ?: emptyList()
                Timber.d("Updated medication ${updatedMedication.medicationId} in UI")
                _isLoading.value = false
            } catch (e: Exception) {
                Timber.e(e, "Failed to update medication ${updatedMedication.medicationId}")
                _error.value = "Failed to update medication: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    private fun handleError(message: String) {
        _error.value = message
    }
}