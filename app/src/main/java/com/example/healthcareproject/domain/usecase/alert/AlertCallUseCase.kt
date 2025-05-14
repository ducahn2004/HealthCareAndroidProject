package com.example.healthcareproject.domain.usecase.alert

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import android.content.pm.PackageManager
import javax.inject.Inject

class AlertCallUseCase @Inject constructor(
    private val context: Context
) {
    fun call(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = "tel:$phoneNumber".toUri()
                }
                context.startActivity(intent)
            } catch (e: SecurityException) {
                Toast.makeText(context, "Permission denied to make calls", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "CALL_PHONE permission is not granted", Toast.LENGTH_SHORT).show()
        }
    }
}