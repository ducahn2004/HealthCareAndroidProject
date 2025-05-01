package com.example.healthcareproject.domain.usecase.appointment

import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.repository.AppointmentRepository
import java.time.LocalDateTime
import javax.inject.Inject

data class AppointmentUseCases @Inject constructor(
    val createAppointment: CreateAppointmentUseCase,
    val getAppointments: GetAppointmentsUseCase,
    val updateAppointment: UpdateAppointmentUseCase,
    val deleteAppointment: DeleteAppointmentUseCase,
    val reminderLogic: AppointmentReminderLogicUseCase
)
