package com.example.healthcareproject.domain.usecase.appointment

import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.repository.AppointmentRepository
import com.example.healthcareproject.domain.repository.UserRepository
import com.example.healthcareproject.domain.usecase.reminder.CreateReminderUseCase
import java.time.LocalDateTime
import javax.inject.Inject

class CreateAppointmentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository,
    private val createReminderUseCase: CreateReminderUseCase
) {
    suspend operator fun invoke(
        doctorName: String,
        location: String,
        appointmentTime: LocalDateTime,
        note: String?
    ): Result<String> {
        return try {
            if (doctorName.isBlank())
                throw IllegalArgumentException("Doctor name cannot be empty")
            if (location.isBlank())
                throw IllegalArgumentException("Location cannot be empty")
            if (appointmentTime.isBefore(LocalDateTime.now()))
                throw IllegalArgumentException("Appointment time cannot be in the past")

            userRepository.refresh()

            val appointmentId = appointmentRepository.createAppointment(
                doctorName = doctorName,
                location = location,
                appointmentTime = appointmentTime,
                note = note
            )

            val message = reminderMessage(doctorName, location, appointmentTime, note)

            createReminderUseCase(
                title = "Appointment Reminder",
                message = message,
                reminderTime = appointmentTime.toLocalTime().minusHours(3),
                repeatPattern = RepeatPattern.Once,
                startDate = appointmentTime.toLocalDate(),
                endDate = appointmentTime.toLocalDate(),
                status = true
            )

            Result.Success(appointmentId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun reminderMessage(
        doctorName: String,
        location: String,
        appointmentTime: LocalDateTime,
        note: String?
    ): String {
        return "Appointment with $doctorName at $location on ${appointmentTime.toLocalDate()} " +
                "at ${appointmentTime.toLocalTime()}" + (note?.let { ". Note: $it" } ?: "")
    }
}