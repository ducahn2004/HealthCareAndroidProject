package com.example.healthcareproject.domain.usecase.appointment

import com.example.healthcareproject.domain.repository.AppointmentRepository
import java.time.LocalDateTime
import javax.inject.Inject

class CreateAppointmentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(
        doctorName: String,
        location: String,
        appointmentTime: LocalDateTime,
        note: String?
    ): String {
        return appointmentRepository.createAppointment(
            doctorName = doctorName,
            location = location,
            appointmentTime = appointmentTime,
            note = note
        )
    }
}