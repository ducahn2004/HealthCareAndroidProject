package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Notification
import com.example.healthcareproject.data.source.network.model.FirebaseNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NotificationFirebaseDataSource : NotificationDataSource {

    override fun observeAll(): Flow<List<FirebaseNotification>> = flowOf(emptyList())

    override fun observeById(notificationId: String): Flow<FirebaseNotification?> = flowOf(null)

    override fun observeByUserId(userId: String): Flow<List<FirebaseNotification>> = flowOf(emptyList())

    override suspend fun getAll(): List<FirebaseNotification> = emptyList()

    override suspend fun getById(notificationId: String): FirebaseNotification? = null

    override suspend fun getByUserId(userId: String): List<FirebaseNotification> = emptyList()

    override suspend fun upsert(notification: Notification) {
        // Add or update the notification in Firebase
    }

    override suspend fun upsertAll(notifications: List<Notification>) {
        // Add or update multiple notifications in Firebase
    }

    override suspend fun deleteById(notificationId: String): Int {
        // Delete the notification by ID in Firebase
        return 0
    }

    override suspend fun deleteByUserId(userId: String): Int {
        // Delete notifications by user ID in Firebase
        return 0
    }

    override suspend fun deleteAll(): Int {
        // Delete all notifications in Firebase
        return 0
    }
}