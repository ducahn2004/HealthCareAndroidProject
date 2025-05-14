package com.example.healthcareproject.domain.usecase.reminder

import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.ReminderRepository
import java.time.LocalTime
import javax.inject.Inject

class UpdateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(
        reminderId: String,
        title: String,
        message: String,
        reminderTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ) {
        reminderRepository.updateReminder(
            reminderId = reminderId,
            title = title,
            message = message,
            reminderTime = reminderTime,
            repeatPattern = repeatPattern,
            status = status
        )
    }
}