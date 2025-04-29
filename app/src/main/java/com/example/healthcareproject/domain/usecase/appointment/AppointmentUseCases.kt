package com.example.healthcareproject.domain.usecase.appointment

data class AppointmentUseCases(
    val createAppointment: CreateAppointmentUseCase,
    val getAppointments: GetAppointmentsUseCase,
    val updateAppointment: UpdateAppointmentUseCase,
    val deleteAppointment: DeleteAppointmentUseCase,
    val reminderLogic: AppointmentReminderLogicUseCase
)
