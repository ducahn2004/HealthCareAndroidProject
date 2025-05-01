package com.example.healthcareproject.present.medicine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.appointment.AppointmentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddAppointmentViewModel @Inject constructor(
    private val appointmentUseCases: AppointmentUseCases
) : ViewModel() {

    // Error handling
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Success state
    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    // Form fields - Đã chuyển thành MutableLiveData công khai
    val diagnosis = MutableLiveData("")
    val doctorName = MutableLiveData("")
    val clinicName = MutableLiveData("")
    val treatment = MutableLiveData("")

    // These can't be directly two-way bound due to complex types
    private val _visitDate = MutableLiveData<LocalDate?>(null)
    val visitDate: LiveData<LocalDate?> = _visitDate

    private val _time = MutableLiveData<LocalTime?>(null)
    val time: LiveData<LocalTime?> = _time

    // Field errors
    private val _diagnosisError = MutableLiveData<String?>(null)
    val diagnosisError: LiveData<String?> = _diagnosisError

    private val _doctorNameError = MutableLiveData<String?>(null)
    val doctorNameError: LiveData<String?> = _doctorNameError

    private val _clinicNameError = MutableLiveData<String?>(null)
    val clinicNameError: LiveData<String?> = _clinicNameError

    private val _treatmentError = MutableLiveData<String?>(null)
    val treatmentError: LiveData<String?> = _treatmentError

    private val _dateError = MutableLiveData<String?>(null)
    val dateError: LiveData<String?> = _dateError

    private val _timeError = MutableLiveData<String?>(null)
    val timeError: LiveData<String?> = _timeError

    // Navigation trigger
    private val _navigateBack = MutableLiveData<Boolean>(false)
    val navigateBack: LiveData<Boolean> = _navigateBack

    // Date/Time picker triggers
    private val _showDatePicker = MutableLiveData<Boolean>(false)
    val showDatePicker: LiveData<Boolean> = _showDatePicker

    private val _showTimePicker = MutableLiveData<Boolean>(false)
    val showTimePicker: LiveData<Boolean> = _showTimePicker

    fun updateVisitDate(visitDate: LocalDate) {
        _visitDate.value = visitDate
        _dateError.value = null
    }

    fun updateTime(time: LocalTime) {
        _time.value = time
        _timeError.value = null
    }

    fun onBackClicked() {
        _navigateBack.value = true
    }

    fun onDateClicked() {
        _showDatePicker.value = true
    }

    fun onTimeClicked() {
        _showTimePicker.value = true
    }

    // Methods to reset picker triggers
    fun resetDatePicker() {
        _showDatePicker.value = false
    }

    fun resetTimePicker() {
        _showTimePicker.value = false
    }

    fun saveAppointment() {
        var hasError = false

        // Validation
        if (diagnosis.value.isNullOrBlank()) {
            _diagnosisError.value = "Required"
            hasError = true
        } else {
            _diagnosisError.value = null
        }

        if (doctorName.value.isNullOrBlank()) {
            _doctorNameError.value = "Required"
            hasError = true
        } else {
            _doctorNameError.value = null
        }

        if (clinicName.value.isNullOrBlank()) {
            _clinicNameError.value = "Required"
            hasError = true
        } else {
            _clinicNameError.value = null
        }

        if (treatment.value.isNullOrBlank()) {
            _treatmentError.value = "Required"
            hasError = true
        } else {
            _treatmentError.value = null
        }

        if (_visitDate.value == null) {
            _dateError.value = "Required"
            hasError = true
        } else {
            _dateError.value = null
        }

        if (_time.value == null) {
            _timeError.value = "Required"
            hasError = true
        } else {
            _timeError.value = null
        }

        if (hasError) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Combine date and time
            val createdAt = LocalDateTime.of(_visitDate.value, _time.value)

            // Call use case
            val result = appointmentUseCases.createAppointment(
                doctorName = doctorName.value ?: "",
                location = clinicName.value ?: "",
                appointmentTime = createdAt,
                note = "${diagnosis.value} - ${treatment.value}"
            )

            when (result) {
                is Result.Success -> {
                    _isLoading.value = false
                    _isSuccess.value = true
                }
                is Result.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.exception.message
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getAppointmentData(): Map<String, Any?> {
        return mapOf(
            "diagnosis" to diagnosis.value,
            "doctorName" to doctorName.value,
            "clinicName" to clinicName.value,
            "treatment" to treatment.value,
            "visitDate" to _visitDate.value,
            "time" to _time.value
        )
    }
}