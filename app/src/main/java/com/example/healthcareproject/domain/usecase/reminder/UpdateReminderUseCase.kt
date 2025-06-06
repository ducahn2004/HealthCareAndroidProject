package com.example.healthcareproject.domain.usecase.reminder

import android.content.Context
import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.ReminderRepository
import com.example.healthcareproject.presentation.util.AlarmManagerUtil
import com.example.healthcareproject.presentation.util.ReminderTimeUtil
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class UpdateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val context: Context
) {
    suspend operator fun invoke(
        reminderId: String,
        title: String,
        message: String,
        reminderTime: LocalTime,
        repeatPattern: RepeatPattern,
        startDate: LocalDate,
        endDate: LocalDate,
        status: Boolean
    ) {

        AlarmManagerUtil.cancelReminderAlarm(context, reminderId)

        reminderRepository.updateReminder(
            reminderId = reminderId,
            title = title,
            message = message,
            reminderTime = reminderTime,
            repeatPattern = repeatPattern,
            startDate = startDate,
            endDate = endDate,
            status = status
        )

        if (!status) return

        reminderRepository.getReminder(reminderId)?.let { updatedReminder ->
            val nextTriggerTime = ReminderTimeUtil.nextTriggerTime(updatedReminder)
            AlarmManagerUtil.setReminderAlarm(context, reminderId, nextTriggerTime)
        }
    }
}
