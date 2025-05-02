package com.example.healthcareproject.domain.usecase.appointment

import com.example.healthcareproject.domain.usecase.alert.CreateAlertUseCase
import com.example.healthcareproject.domain.model.RepeatPattern
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class AppointmentReminderLogicUseCase @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val createAlertUseCase: CreateAlertUseCase
) {
    suspend operator fun invoke() {
        val currentTime = LocalDateTime.now()
        val appointments = getAppointmentsUseCase()

        appointments.forEach { appointment ->
            val timeDifference = ChronoUnit.HOURS.between(currentTime, appointment.appointmentTime)
            if (timeDifference in 0..3) {
                createAlertUseCase(
                    title = "Appointment Reminder",
                    message = "You have an appointment with Dr. ${appointment.doctorName} at ${appointment.location}.",
                    alertTime = appointment.appointmentTime.toLocalTime(),
                    repeatPattern = RepeatPattern.None,
                    status = true
                )
            }
        }
    }
}