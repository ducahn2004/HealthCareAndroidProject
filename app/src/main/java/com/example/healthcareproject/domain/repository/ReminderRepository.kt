package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.Reminder
import com.example.healthcareproject.domain.model.RepeatPattern
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

/**
 * Interface to the data layer for reminders.
 */
interface ReminderRepository {

    suspend fun createReminder(
        title: String,
        message: String,
        reminderTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ): String

    suspend fun updateReminder(
        reminderId: String,
        title: String,
        message: String,
        reminderTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    )

    suspend fun deleteReminder(reminderId: String)

    suspend fun getReminder(reminderId: String, forceUpdate: Boolean = false): Reminder?

    suspend fun getReminders(forceUpdate: Boolean = false): List<Reminder>

    fun getRemindersStream(): Flow<List<Reminder>>

    fun getRemindersStream(reminderId: String): Flow<Reminder?>

    suspend fun refresh()

    suspend fun refreshReminder(reminderId: String)

    suspend fun activateReminder(reminderId: String)

    suspend fun deactivateReminder(reminderId: String)

    suspend fun clearInactiveReminders()

    suspend fun deleteAllReminders()
}