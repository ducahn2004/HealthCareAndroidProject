package com.example.healthcareproject.present.medicine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.usecase.appointment.AppointmentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddAppointmentViewModel @Inject constructor(
    private val appointmentUseCases: AppointmentUseCases
) : ViewModel() {
    fun saveAppointment(
        doctorName: String,
        location: String,
        appointmentTime: LocalDateTime,
        note: String?
    ) {
        viewModelScope.launch {
            appointmentUseCases.createAppointment(doctorName, location, appointmentTime, note)
                .collect { result ->
                    // Handle result (e.g., navigate back on success)
                }
        }
    }
}