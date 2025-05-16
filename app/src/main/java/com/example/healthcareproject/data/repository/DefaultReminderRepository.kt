package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.ReminderDao
import com.example.healthcareproject.data.source.network.datasource.ReminderDataSource
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.di.ApplicationScope
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.Reminder
import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.ReminderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultReminderRepository @Inject constructor(
    private val networkDataSource: ReminderDataSource,
    private val localDataSource: ReminderDao,
    private val authDataSource: AuthDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : ReminderRepository {

    private val userId: String
        get() = authDataSource.getCurrentUserId() ?: throw Exception("User not logged in")

    override suspend fun createReminder(
        title: String,
        message: String,
        reminderTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ): String {
        val reminderId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val reminder = Reminder(
            reminderId = reminderId,
            userId = userId,
            title = title,
            message = message,
            reminderTime = reminderTime,
            repeatPattern = repeatPattern,
            status = status,
            createdAt = LocalDateTime.now()
        )
        localDataSource.upsert(reminder.toLocal())
        saveRemindersToNetwork()
        return reminderId
    }

    override suspend fun updateReminder(
        reminderId: String,
        title: String,
        message: String,
        reminderTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ) {
        val reminder = getReminder(reminderId)?.copy(
            title = title,
            message = message,
            reminderTime = reminderTime,
            repeatPattern = repeatPattern,
            status = status
        ) ?: throw Exception("Reminder (id $reminderId) not found")

        localDataSource.upsert(reminder.toLocal())
        saveRemindersToNetwork()
    }

    override suspend fun deleteReminder(reminderId: String) {
        localDataSource.deleteById(reminderId)
        saveRemindersToNetwork()
    }

    override suspend fun getReminder(reminderId: String, forceUpdate: Boolean): Reminder? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(reminderId)?.toExternal()
    }

    override suspend fun getReminders(forceUpdate: Boolean): List<Reminder> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override fun getRemindersStream(): Flow<List<Reminder>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getRemindersStream(reminderId: String): Flow<Reminder?> {
        return localDataSource.observeById(reminderId)
            .map { it?.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteReminders = networkDataSource.loadReminders(userId)
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteReminders.toLocal())
        }
    }

    override suspend fun refreshReminder(reminderId: String) {
        refresh()
    }

    override suspend fun activateReminder(reminderId: String) {
        val reminder = getReminder(reminderId)?.copy(status = true)
            ?: throw Exception("Reminder (id $reminderId) not found")
        localDataSource.upsert(reminder.toLocal())
        saveRemindersToNetwork()
    }

    override suspend fun deactivateReminder(reminderId: String) {
        val reminder = getReminder(reminderId)?.copy(status = false)
            ?: throw Exception("Reminder (id $reminderId) not found")
        localDataSource.upsert(reminder.toLocal())
        saveRemindersToNetwork()
    }

    override suspend fun clearInactiveReminders() {
        localDataSource.deleteInactive()
        saveRemindersToNetwork()
    }

    override suspend fun deleteAllReminders() {
        localDataSource.deleteAll()
        saveRemindersToNetwork()
    }

    private fun saveRemindersToNetwork() {
        scope.launch {
            try {
                val localReminders = localDataSource.getAll()
                val networkReminders = withContext(dispatcher) {
                    localReminders.toNetwork()
                }
                networkDataSource.saveReminders(networkReminders)
            } catch (e: Exception) {
                // Log or handle the exception
            }
        }
    }
}