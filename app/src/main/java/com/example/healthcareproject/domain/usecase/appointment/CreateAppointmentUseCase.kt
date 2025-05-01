package com.example.healthcareproject.domain.usecase.appointment

import com.example.healthcareproject.domain.model.Result
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
    ): Result<String> {
        return try {
            // Validate inputs
            if (doctorName.isBlank()) throw IllegalArgumentException("Doctor name cannot be empty")
            if (location.isBlank()) throw IllegalArgumentException("Location cannot be empty")
            if (appointmentTime.isBefore(LocalDateTime.now())) throw IllegalArgumentException("Appointment time cannot be in the past")

            // Call repository to create appointment
            val appointmentId = appointmentRepository.createAppointment(
                doctorName = doctorName,
                location = location,
                appointmentTime = appointmentTime,
                note = note
            )

            Result.Success(appointmentId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}