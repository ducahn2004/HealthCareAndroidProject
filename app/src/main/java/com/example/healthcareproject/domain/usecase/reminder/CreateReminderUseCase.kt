package com.example.healthcareproject.domain.usecase.reminder

import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.ReminderRepository
import java.time.LocalTime
import javax.inject.Inject

class CreateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(
        title: String,
        message: String,
        reminderTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ): String {
        return reminderRepository.createReminder(
            title = title,
            message = message,
            reminderTime = reminderTime,
            repeatPattern = repeatPattern,
            status = status
        )
    }
}