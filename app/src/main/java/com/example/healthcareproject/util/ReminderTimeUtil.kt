package com.example.healthcareproject.util

import com.example.healthcareproject.domain.model.Reminder
import com.example.healthcareproject.domain.model.RepeatPattern
import java.time.LocalDateTime

class ReminderTimeUtil {

    companion object {
        fun nextTriggerTime(reminder: Reminder): LocalDateTime {
            val now = LocalDateTime.now()
            var nextTime = now.withHour(reminder.reminderTime.hour)
                .withMinute(reminder.reminderTime.minute)
                .withSecond(0)
                .withNano(0)

            if (nextTime.isBefore(now)) {
                nextTime = when (reminder.repeatPattern) {
                    RepeatPattern.Daily -> nextTime.plusDays(1)
                    RepeatPattern.Weekly -> nextTime.plusWeeks(1)
                    else -> nextTime  // NONE: still keep the past time
                }
            }

            return nextTime
        }
    }
}