package com.example.healthcareproject.domain.usecase.reminder

import android.content.Context
import com.example.healthcareproject.presentation.util.ReminderTimeUtil
import com.example.healthcareproject.presentation.util.AlarmManagerUtil
import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.ReminderRepository
import com.example.healthcareproject.presentation.util.ExactAlarmPermissionUtil
import java.time.LocalDate
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
        startDate: LocalDate,
        endDate: LocalDate,
        status: Boolean,
    ): String {
        val reminderId = reminderRepository.createReminder(
            title = title,
            message = message,
            reminderTime = reminderTime,
            repeatPattern = repeatPattern,
            startDate = startDate,
            endDate = endDate,
            status = status,
        )

        val reminder = reminderRepository.getReminder(reminderId) ?: return reminderId

        val nextTriggerTime = ReminderTimeUtil.nextTriggerTime(reminder)

        val hasPermission = ExactAlarmPermissionUtil.checkAndRequestPermission(context)
        if (hasPermission) {
            AlarmManagerUtil.setReminderAlarm(context, reminder.reminderId, nextTriggerTime)
        }

        return reminderId
    }
}
