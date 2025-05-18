package com.example.healthcareproject.presentation.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import timber.log.Timber

object AlertManagerUtil {
    fun makeCall(context: Context, phoneNumber: String) {
        if (PermissionManager.hasPermission(context, PermissionManager.REQUEST_CALL_PHONE)) {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = "tel:$phoneNumber".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            Timber.tag("AlertManagerUtil").d("Calling $phoneNumber")
            context.startActivity(intent)
        } else {
            Timber.tag("AlertManagerUtil").d("Call permission not granted")
            PermissionManager.showPermissionSettingsDialog(
                context,
                "Permission Required",
                "App needs CALL_PHONE permission to call emergency contact."
            )
        }
    }
}
