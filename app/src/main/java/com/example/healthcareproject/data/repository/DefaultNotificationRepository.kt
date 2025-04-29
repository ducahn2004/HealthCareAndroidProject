package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.NotificationDao
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.data.source.network.datasource.NotificationDataSource
import com.example.healthcareproject.di.ApplicationScope
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.Notification
import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable
import com.example.healthcareproject.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNotificationRepository @Inject constructor(
    private val networkDataSource: NotificationDataSource,
    private val localDataSource: NotificationDao,
    private val authDataSource: AuthDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : NotificationRepository {

    private val userId: String
        get() = authDataSource.getCurrentUserId() ?: throw Exception("User not logged in")

    override suspend fun createNotification(
        type: NotificationType,
        relatedTable: RelatedTable,
        relatedId: String,
        message: String,
        notificationTime: LocalDateTime
    ): String {
        val notificationId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val notification = Notification(
            notificationId = notificationId,
            userId = userId,
            type = type,
            relatedTable = relatedTable,
            relatedId = relatedId,
            message = message,
            timestamp = notificationTime
        )
        localDataSource.upsert(notification.toLocal())
        saveNotificationsToNetwork()
        return notificationId
    }

    override suspend fun updateNotification(
        notificationId: String,
        type: NotificationType,
        relatedTable: RelatedTable,
        relatedId: String,
        message: String,
        notificationTime: LocalDateTime
    ) {
        val notification = getNotification(notificationId)?.copy(
            type = type,
            relatedTable = relatedTable,
            relatedId = relatedId,
            message = message,
            timestamp = notificationTime
        ) ?: throw Exception("Notification (id $notificationId) not found")

        localDataSource.upsert(notification.toLocal())
        saveNotificationsToNetwork()
    }

    override fun getNotificationsStream(): Flow<List<Notification>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getNotificationStream(notificationId: String): Flow<Notification?> {
        return localDataSource.observeById(notificationId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun getNotifications(forceUpdate: Boolean): List<Notification> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteNotifications = networkDataSource.loadNotifications(userId)
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteNotifications.toLocal())
        }
    }

    override suspend fun getNotification(notificationId: String, forceUpdate: Boolean): Notification? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(notificationId)?.toExternal()
    }

    override suspend fun refreshNotification(notificationId: String) {
        refresh()
    }

    override suspend fun deleteAllNotifications() {
        localDataSource.deleteAll()
        saveNotificationsToNetwork()
    }

    override suspend fun deleteNotification(notificationId: String) {
        localDataSource.deleteById(notificationId)
        saveNotificationsToNetwork()
    }

    private fun saveNotificationsToNetwork() {
        scope.launch {
            try {
                val localNotifications = localDataSource.getAll()
                val networkNotifications = withContext(dispatcher) {
                    localNotifications.toNetwork()
                }
                networkDataSource.saveNotifications(networkNotifications)
            } catch (e: Exception) {
                // Log or handle the exception
            }
        }
    }
}