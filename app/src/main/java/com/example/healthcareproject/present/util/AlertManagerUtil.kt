package com.example.healthcareproject.present.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

object AlertManagerUtil {
    fun makeCall(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = "tel:$phoneNumber".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(intent)
        } else {
            // Có thể gửi notification yêu cầu cấp quyền
        }
    }
}
