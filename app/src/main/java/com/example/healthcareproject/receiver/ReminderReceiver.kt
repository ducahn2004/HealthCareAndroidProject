package com.example.healthcareproject.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.example.healthcareproject.domain.repository.ReminderRepository
import com.example.healthcareproject.util.NotificationUtil
import com.example.healthcareproject.util.AlarmManagerUtil
import com.example.healthcareproject.util.ReminderTimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver(
    private val reminderRepository: ReminderRepository
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra("reminderId") ?: return

        CoroutineScope(Dispatchers.IO).launch {
            // 1. Lấy Reminder từ database
            val reminder = reminderRepository.getReminder(reminderId) ?: return@launch

            // 2. Hiển thị thông báo
            NotificationUtil.showReminderNotification(
                context = context,
                reminderId = reminder.reminderId,
                title = reminder.title,
                message = reminder.message
            )

            // 3. Tính toán thời gian nhắc nhở tiếp theo
            val nextTime = ReminderTimeUtil.nextTriggerTime(reminder)

            // 4. Đặt lại alarm cho lần sau
            AlarmManagerUtil.setReminderAlarm(
                context = context,
                reminderId = reminder.reminderId,
                triggerTime = nextTime
            )
        }
    }
}
