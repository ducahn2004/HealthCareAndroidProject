package com.example.healthcareproject.domain.usecase.reminder

import com.example.healthcareproject.domain.repository.ReminderRepository
import javax.inject.Inject

class DeleteReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(reminderId: String) {
        reminderRepository.deleteReminder(reminderId)
    }
}