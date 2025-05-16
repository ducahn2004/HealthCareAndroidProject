package com.example.healthcareproject.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable
import com.example.healthcareproject.domain.repository.NotificationRepository
import com.example.healthcareproject.domain.repository.ReminderRepository
import com.example.healthcareproject.presentation.util.NotificationUtil
import com.example.healthcareproject.presentation.util.AlarmManagerUtil
import com.example.healthcareproject.presentation.util.ReminderTimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ReminderReceiver(
    private val reminderRepository: ReminderRepository,
    private val notificationRepository: NotificationRepository
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra("reminderId") ?: return

        CoroutineScope(Dispatchers.IO).launch {

            val reminder = reminderRepository.getReminder(reminderId) ?: return@launch

            NotificationUtil.showReminderNotification(
                context = context,
                reminderId = reminder.reminderId,
                title = reminder.title,
                message = reminder.message
            )

            notificationRepository.createNotification(
                type = NotificationType.Reminder,
                relatedTable = RelatedTable.Reminder,
                relatedId = reminder.reminderId,
                message = reminder.message,
                notificationTime = LocalDateTime.now()
            )

            val nextTime = ReminderTimeUtil.nextTriggerTime(reminder)

            AlarmManagerUtil.cancelReminderAlarm(context, reminder.reminderId)

            AlarmManagerUtil.setReminderAlarm(
                context = context,
                reminderId = reminder.reminderId,
                triggerTime = nextTime
            )
        }
    }
}
