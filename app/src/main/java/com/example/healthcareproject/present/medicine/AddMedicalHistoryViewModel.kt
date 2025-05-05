package com.example.healthcareproject.present.medicine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.medicalvisit.MedicalVisitUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddMedicalHistoryViewModel @Inject constructor(
    private val medicalVisitUseCases: MedicalVisitUseCases
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    fun addMedicalVisit(
        patientName: String,
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        diagnosis: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = medicalVisitUseCases.createMedicalVisitUseCase(
                    patientName = patientName,
                    visitReason = visitReason,
                    visitDate = visitDate,
                    doctorName = doctorName,
                    diagnosis = diagnosis
                )
                if (result is Result.Success) {
                    _isSuccess.value = true
                    _error.value = null
                } else if (result is Result.Error) {
                    _isSuccess.value = false
                    _error.value = result.exception.message
                }
            } catch (e: Exception) {
                _isSuccess.value = false
                _error.value = e.message ?: "Error adding medical visit"
            } finally {
                _isLoading.value = false
            }
        }
    }
}