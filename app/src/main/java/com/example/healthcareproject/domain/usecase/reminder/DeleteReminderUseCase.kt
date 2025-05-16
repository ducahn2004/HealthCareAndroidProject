package com.example.healthcareproject.domain.usecase.reminder

import android.content.Context
import com.example.healthcareproject.domain.repository.ReminderRepository
import com.example.healthcareproject.present.util.AlarmManagerUtil
import javax.inject.Inject

class DeleteReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val context: Context
) {
    suspend operator fun invoke(reminderId: String) {

        AlarmManagerUtil.cancelReminderAlarm(context, reminderId)

        reminderRepository.deleteReminder(reminderId)
    }
}