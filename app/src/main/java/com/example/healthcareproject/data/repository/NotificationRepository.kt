package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.source.local.entity.Notification
import com.example.healthcareproject.data.source.network.datasource.NotificationDataSource
import com.example.healthcareproject.data.source.network.model.FirebaseNotification
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDataSource: NotificationDataSource) {

    fun observeAll(): Flow<List<FirebaseNotification>> = notificationDataSource.observeAll()

    fun observeById(notificationId: String): Flow<FirebaseNotification?> = notificationDataSource.observeById(notificationId)

    suspend fun getAll(): List<FirebaseNotification> = notificationDataSource.getAll()

    suspend fun getById(notificationId: String): FirebaseNotification? = notificationDataSource.getById(notificationId)

    suspend fun upsert(notification: Notification) = notificationDataSource.upsert(notification)

    suspend fun upsertAll(notifications: List<Notification>) = notificationDataSource.upsertAll(notifications)

    suspend fun deleteById(notificationId: String): Int = notificationDataSource.deleteById(notificationId)

    suspend fun deleteAll(): Int = notificationDataSource.deleteAll()
}