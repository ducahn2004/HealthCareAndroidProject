package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseNotification
import kotlinx.coroutines.tasks.await

class NotificationFirebaseDataSource : NotificationDataSource {

    private val notificationRef = FirebaseService.getReference("notifications")
    override suspend fun writeNotification(notification: FirebaseNotification) {
        notificationRef.child(notification.notificationId).setValue(notification).await()
    }

    override suspend fun readNotification(notificationId: String): FirebaseNotification? {
        val snapshot = notificationRef.child(notificationId).get().await()
        return snapshot.getValue(FirebaseNotification::class.java)
    }

    override suspend fun deleteNotification(notificationId: String) {
        notificationRef.child(notificationId).removeValue().await()
    }

    override suspend fun updateNotification(
        notificationId: String,
        notification: FirebaseNotification
    ) {
        notificationRef.child(notificationId).setValue(notification).await()
    }

    override suspend fun readAllNotificationsByUserId(userId: String): List<FirebaseNotification> {
        val snapshot = notificationRef.orderByChild("userId").equalTo(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(FirebaseNotification::class.java) }
    }

}