package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.Notification
import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Interface to the data layer for notifications.
 */
interface NotificationRepository {

    suspend fun createNotification(
        type: NotificationType,
        relatedTable: RelatedTable,
        relatedId: String,
        message: String,
        notificationTime: LocalDateTime
    ): String

    suspend fun updateNotification(
        notificationId: String,
        type: NotificationType,
        relatedTable: RelatedTable,
        relatedId: String,
        message: String,
        notificationTime: LocalDateTime
    )

    fun getNotificationsStream(): Flow<List<Notification>>

    fun getNotificationStream(notificationId: String): Flow<Notification?>

    suspend fun getNotifications(forceUpdate: Boolean = false): List<Notification>

    suspend fun refresh()

    suspend fun getNotification(notificationId: String, forceUpdate: Boolean = false): Notification?

    suspend fun refreshNotification(notificationId: String)

    suspend fun deleteAllNotifications()

    suspend fun deleteNotification(notificationId: String)
}