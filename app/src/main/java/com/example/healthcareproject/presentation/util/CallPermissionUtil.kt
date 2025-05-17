package com.example.healthcareproject.presentation.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object CallPermissionUtil {

    private const val CALL_PERMISSION_CHANNEL_ID = "call_permission_channel"
    private const val CALL_PERMISSION_NOTIFICATION_ID = 1234

    fun hasCallPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun notifyRequestCallPermission(context: Context) {
        NotificationUtil.showPermissionRequestNotification(
            context = context,
            channelId = CALL_PERMISSION_CHANNEL_ID,
            channelName = "Call Permission Required",
            notificationId = CALL_PERMISSION_NOTIFICATION_ID,
            title = "Permission Required",
            message = "The app needs CALL_PHONE permission to function. Please grant it in Settings."
        )
    }
}
