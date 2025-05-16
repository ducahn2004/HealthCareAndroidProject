package com.example.healthcareproject.domain.usecase.reminder

import android.content.Context
import com.example.healthcareproject.util.ReminderTimeUtil
import com.example.healthcareproject.util.AlarmManagerUtil
import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.ReminderRepository
import java.time.LocalTime
import javax.inject.Inject

class CreateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val context: Context
) {
    suspend operator fun invoke(
        title: String,
        message: String,
        reminderTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean,
    ): String {
        val reminderId = reminderRepository.createReminder(
            title = title,
            message = message,
            reminderTime = reminderTime,
            repeatPattern = repeatPattern,
            status = status,
        )

        val reminder = reminderRepository.getReminder(reminderId) ?: return reminderId

        val nextTriggerTime = ReminderTimeUtil.nextTriggerTime(reminder)

        AlarmManagerUtil.setReminderAlarm(context, reminder.reminderId, nextTriggerTime)

        return reminderId
    }
}