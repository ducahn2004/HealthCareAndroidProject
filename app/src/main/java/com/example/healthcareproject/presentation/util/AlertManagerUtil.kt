package com.example.healthcareproject.presentation.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

object AlertManagerUtil {
    fun makeCall(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = "tel:$phoneNumber".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (CallPermissionUtil.hasCallPermission(context)) {
            context.startActivity(intent)
        } else {
            CallPermissionUtil.notifyRequestCallPermission(context)
        }
    }
}
