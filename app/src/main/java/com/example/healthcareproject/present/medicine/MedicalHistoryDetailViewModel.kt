package com.example.healthcareproject.present.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.usecase.medicalvisit.MedicalVisitUseCases
import com.example.healthcareproject.domain.usecase.medication.MedicationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@HiltViewModel
class MedicalHistoryDetailViewModel @Inject constructor(
    private val medicalVisitUseCases: MedicalVisitUseCases,
    private val medicationUseCases: MedicationUseCases
) : ViewModel() {

    // Medical Visit details
    private val _medicalVisit = MutableLiveData<MedicalVisit?>()
    val medicalVisit: LiveData<MedicalVisit?> = _medicalVisit

    // Medications list
    private val _medications = MutableLiveData<List<Medication>>(emptyList())
    val medications: LiveData<List<Medication>> = _medications

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error message
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    // Formatted date and time
    private val _formattedDate = MutableLiveData<String>("")
    val formattedDate: LiveData<String> = _formattedDate

    private val _formattedTime = MutableLiveData<String>("")
    val formattedTime: LiveData<String> = _formattedTime

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    fun loadDetails(visitId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val medicalVisit = medicalVisitUseCases.getMedicalVisitUseCase(visitId)
                val medicationsList = medicationUseCases.getMedicationsByVisitId(visitId)

                if (medicalVisit != null) {
                    _medicalVisit.value = medicalVisit
                    _medications.value = medicationsList
                    _formattedDate.value = medicalVisit.visitDate.format(dateFormatter)
                    _formattedTime.value = medicalVisit.createdAt.format(timeFormatter)
                    _error.value = null
                } else {
                    _error.value = "Medical visit not found"
                    _medications.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}