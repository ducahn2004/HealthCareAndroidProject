package com.example.healthcareproject.present.viewmodel.medicine

import androidx.lifecycle.*
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.usecase.medicalvisit.GetMedicalVisitUseCase
import com.example.healthcareproject.domain.usecase.medication.GetMedicationsByVisitIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MedicalHistoryDetailViewModel @Inject constructor(
    private val getMedicalVisitUseCase: GetMedicalVisitUseCase,
    private val getMedicationsByVisitId: GetMedicationsByVisitIdUseCase
) : ViewModel() {

    private val _medicalVisit = MutableLiveData<MedicalVisit?>()
    val medicalVisit: LiveData<MedicalVisit?> get() = _medicalVisit

    private val _medications = MutableLiveData<List<Medication>>(emptyList())
    val medications: LiveData<List<Medication>> get() = _medications

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

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
                val medicalVisit = getMedicalVisitUseCase(visitId)
                val medicationsList = getMedicationsByVisitId(visitId)
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
    private fun handleError(message: String) {
        _error.value = message
    }

    fun updateMedication(updatedMedication: Medication) {

    }

    fun deleteMedication(medicationId: String) {

    }


}