package com.example.healthcareproject.presentation.viewmodel.medicine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.usecase.appointment.CreateAppointmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddAppointmentViewModel @Inject constructor(
    private val createAppointmentUseCase: CreateAppointmentUseCase
) : ViewModel() {

    // Error handling
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Success state with MedicalVisit
    private val _successWithVisit = MutableLiveData<MedicalVisit?>(null)
    val successWithVisit: LiveData<MedicalVisit?> = _successWithVisit

    // Form fields
    val diagnosis = MutableLiveData("")
    val doctorName = MutableLiveData("")
    val clinicName = MutableLiveData("")
    val treatment = MutableLiveData("")

    // Date and time
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
        _navigateBack.postValue(false)
    }

    fun resetNavigateBack() {
        _navigateBack.value = false
    }

    fun onDateClicked() {
        _showDatePicker.value = true
    }

    fun onTimeClicked() {
        _showTimePicker.value = true
    }

    fun resetDatePicker() {
        _showDatePicker.value = false
    }

    fun resetTimePicker() {
        _showTimePicker.value = false
    }

    fun saveAppointment() {
        var hasError = false
        Timber.tag("AddAppointment")
            .d("Saving: Diagnosis=${diagnosis.value}, Doctor=${doctorName.value}, Clinic=${clinicName.value}, Treatment=${treatment.value}, Date=${_visitDate.value}, Time=${_time.value}")

        // Validation
        if (diagnosis.value.isNullOrBlank()) {
            _diagnosisError.value = "Diagnosis is required"
            hasError = true
        } else {
            _diagnosisError.value = null
        }

        if (doctorName.value.isNullOrBlank()) {
            _doctorNameError.value = "Doctor name is required"
            hasError = true
        } else {
            _doctorNameError.value = null
        }

        if (clinicName.value.isNullOrBlank()) {
            _clinicNameError.value = "Clinic name is required"
            hasError = true
        } else {
            _clinicNameError.value = null
        }

        if (_visitDate.value == null) {
            _dateError.value = "Date is required"
            hasError = true
        } else {
            _dateError.value = null
        }

        if (_time.value == null) {
            _timeError.value = "Time is required"
            hasError = true
        } else {
            _timeError.value = null
        }

        if (hasError) {
            Timber.tag("AddAppointment").d("Validation failed")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val createdAt = LocalDateTime.of(_visitDate.value, _time.value)
                val result = createAppointmentUseCase(
                    doctorName = doctorName.value ?: "",
                    location = clinicName.value ?: "",
                    appointmentTime = createdAt,
                    note = "${diagnosis.value}"
                )

                when (result) {
                    is Result.Success -> {
                        Timber.tag("AddAppointment").d("Save success: ${result.data}")
                        val medicalVisit = MedicalVisit(
                            visitId = result.data,
                            userId = "",
                            visitDate = _visitDate.value!!,
                            clinicName = clinicName.value!!,
                            doctorName = doctorName.value!!,
                            diagnosis = diagnosis.value!!,
                            treatment = treatment.value!!,
                            createdAt = createdAt
                        )
                        _successWithVisit.value = medicalVisit
                        //_successWithVisit.value = null
                    }
                    is Result.Error -> {
                        Timber.tag("AddAppointment").e("Save failed: ${result.exception.message}")
                        _errorMessage.value = result.exception.message ?: "Failed to save appointment"
                    }
                    is Result.Loading -> {
                        Timber.tag("AddAppointment").d("Saving in progress")
                    }
                }
            } catch (e: Exception) {
                Timber.tag("AddAppointment").e("Exception: ${e.message}")
                _errorMessage.value = "Error saving appointment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun resetSuccessWithVisit() {
        _successWithVisit.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}