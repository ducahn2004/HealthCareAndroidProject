package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseNotification

interface NotificationDataSource {

    suspend fun writeNotification(notification: FirebaseNotification)

    suspend fun readNotification(notificationId: String): FirebaseNotification?

    suspend fun deleteNotification(notificationId: String)

    suspend fun updateNotification(notificationId: String, notification: FirebaseNotification)

    suspend fun readAllNotificationsByUserId(userId: String): List<FirebaseNotification>?
}