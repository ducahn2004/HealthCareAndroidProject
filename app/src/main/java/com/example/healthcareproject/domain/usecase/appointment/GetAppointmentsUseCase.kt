package com.example.healthcareproject.domain.usecase.appointment

import com.example.healthcareproject.domain.model.Appointment
import com.example.healthcareproject.domain.repository.AppointmentRepository
import javax.inject.Inject

class GetAppointmentsUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(): List<Appointment> {
        return appointmentRepository.getAppointments()
    }
}