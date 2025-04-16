package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Notification
import com.example.healthcareproject.data.source.network.model.FirebaseNotification
import kotlinx.coroutines.flow.Flow

interface NotificationDataSource {
    fun observeAll(): Flow<List<FirebaseNotification>>

    fun observeById(notificationId: String): Flow<FirebaseNotification?>

    fun observeByUserId(userId: String): Flow<List<FirebaseNotification>>

    suspend fun getAll(): List<FirebaseNotification>

    suspend fun getById(notificationId: String): FirebaseNotification?

    suspend fun getByUserId(userId: String): List<FirebaseNotification>

    suspend fun upsert(notification: Notification)

    suspend fun upsertAll(notifications: List<Notification>)

    suspend fun deleteById(notificationId: String): Int

    suspend fun deleteByUserId(userId: String): Int

    suspend fun deleteAll(): Int
}