package com.example.healthcareproject.domain.usecase.reminder

import com.example.healthcareproject.domain.model.Reminder
import com.example.healthcareproject.domain.repository.ReminderRepository
import javax.inject.Inject

class GetRemindersUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(): List<Reminder> {
        return reminderRepository.getReminders()
    }
}