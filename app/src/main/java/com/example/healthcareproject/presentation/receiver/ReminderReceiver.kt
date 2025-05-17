package com.example.healthcareproject.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthcareproject.di.ReceiverEntryPoint
import com.example.healthcareproject.presentation.util.NotificationUtil
import com.example.healthcareproject.presentation.util.AlarmManagerUtil
import com.example.healthcareproject.presentation.util.ReminderTimeUtil
import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "ACTION_REMINDER") {
            Timber.tag("ReminderReceiver").d("Received unexpected action: ${intent.action}")
            return
        }

        val reminderId = intent.getStringExtra("reminderId")
        Timber.tag("ReminderReceiver").d("onReceive triggered for reminderId=$reminderId")
        if (reminderId == null) {
            Timber.tag("ReminderReceiver").e("No reminderId in intent")
            return
        }
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ReceiverEntryPoint::class.java
        )

        val reminderRepository = entryPoint.reminderRepository()
        val notificationRepository = entryPoint.notificationRepository()

        CoroutineScope(Dispatchers.IO).launch {
            val reminder = reminderRepository.getReminder(reminderId) ?: return@launch

            if (!isTodayInRange(reminder.startDate, reminder.endDate)) {
                AlarmManagerUtil.cancelReminderAlarm(context, reminderId)
                return@launch
            }

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

    private fun isTodayInRange(startDate: LocalDate, endDate: LocalDate): Boolean {
        val today = LocalDate.now()
        return !today.isBefore(startDate) && !today.isAfter(endDate)
    }
}
