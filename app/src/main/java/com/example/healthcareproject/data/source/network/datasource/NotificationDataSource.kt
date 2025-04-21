package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseNotification

interface NotificationDataSource {

    suspend fun loadNotifications(userId: String): List<FirebaseNotification>

    suspend fun saveNotifications(notifications: List<FirebaseNotification>)
}