package com.example.healthcareproject.domain.usecase.reminder

data class ReminderUseCases(
    val createReminder: CreateReminderUseCase,
    val getReminders: GetRemindersUseCase,
    val updateReminder: UpdateReminderUseCase,
    val deleteReminder: DeleteReminderUseCase,
    val getReminderById: GetReminderByIdUseCase,
)