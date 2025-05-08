package com.example.healthcareproject.present.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.healthcareproject.R
import com.example.healthcareproject.domain.model.NotificationType
import com.example.healthcareproject.domain.model.RelatedTable
import com.example.healthcareproject.domain.usecase.notification.CreateNotificationUseCase
import com.example.healthcareproject.present.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var createNotificationUseCase: CreateNotificationUseCase

    private val channelId = "healthcare_notifications"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("FCM").d("FCM Token: $token")
        // Optionally send token to server
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.tag("FCM")
            .d("Received message: ${remoteMessage.data}, notification: ${remoteMessage.notification?.title}")

        // Extract notification fields
        val title = remoteMessage.notification?.title ?: "New Notification"
        val body = remoteMessage.notification?.body ?: "You have a new notification"
        val type = remoteMessage.data["type"]?.let {
            try {
                NotificationType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                Timber.tag("FCM").e("Invalid NotificationType: $it")
                NotificationType.None
            }
        } ?: NotificationType.None
        val relatedId = remoteMessage.data["relatedId"] ?: ""
        val relatedTable = remoteMessage.data["relatedTable"]?.let {
            try {
                RelatedTable.valueOf(it)
            } catch (e: IllegalArgumentException) {
                Timber.tag("FCM").e("Invalid RelatedTable: $it")
                RelatedTable.None
            }
        } ?: RelatedTable.None
        val notificationId = remoteMessage.messageId ?: System.currentTimeMillis().toString()

        // Save notification to database if valid
        if (relatedId.isNotEmpty() && relatedTable != RelatedTable.None) {
            saveNotificationToDatabase(notificationId, title, body, type, relatedId, relatedTable)
        } else {
            Timber.tag("FCM").w("Skipping database save due to missing relatedId or relatedTable")
        }

        // Show push notification
        showNotification(title, body, notificationId)
    }

    private fun saveNotificationToDatabase(
        notificationId: String,
        title: String,
        message: String,
        type: NotificationType,
        relatedId: String,
        relatedTable: RelatedTable
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                createNotificationUseCase(
                    type = type,
                    message = message,
                    notificationTime = LocalDateTime.now(),
                    relatedTable = relatedTable,
                    relatedId = relatedId
                )
                Timber.tag("FCM").d("Notification saved: $notificationId")
            } catch (e: Exception) {
                Timber.tag("FCM").e("Failed to save notification: $e")
            }
        }
    }

    private fun showNotification(title: String, message: String, notificationId: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Healthcare Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for healthcare app"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notificationId", notificationId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show notification
        notificationManager.notify(notificationId.hashCode(), notification)
    }
}