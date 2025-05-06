package com.example.healthcareproject.domain.usecase.appointment

import javax.inject.Inject

data class AppointmentUseCases @Inject constructor(
    val createAppointment: CreateAppointmentUseCase,
    val getAppointments: GetAppointmentsUseCase,
    val updateAppointment: UpdateAppointmentUseCase,
    val deleteAppointment: DeleteAppointmentUseCase,
    val reminderLogic: AppointmentReminderLogicUseCase
)
