package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseNotification
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationFirebaseDataSource @Inject constructor(
    firebaseDatabase: FirebaseDatabase
) : NotificationDataSource {

    private val notificationRef = firebaseDatabase.getReference("notifications")

    override suspend fun loadNotifications(userId: String): List<FirebaseNotification> = try {
        notificationRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseNotification::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading notifications for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveNotifications(notifications: List<FirebaseNotification>) {
        if (notifications.isEmpty()) return

        try {
            val updates = notifications.associateBy { it.notificationId }
            notificationRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving notifications: ${e.message}", e)
        }
    }
}