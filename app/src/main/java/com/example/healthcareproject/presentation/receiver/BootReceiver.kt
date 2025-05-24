package com.example.healthcareproject.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthcareproject.presentation.service.MeasurementMonitorService
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != null) {
                val serviceIntent = Intent(context, MeasurementMonitorService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
                Timber.tag("BootReceiver")
                    .d("User logged in, service started after boot")
            } else {
                Timber.tag("BootReceiver")
                    .d("User not logged in, service not started after boot")
            }
        }
        Timber.tag("BootReceiver").d("BootReceiver triggered with action: ${intent?.action}")
    }
}
