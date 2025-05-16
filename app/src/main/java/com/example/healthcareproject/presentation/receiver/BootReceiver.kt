package com.example.healthcareproject.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthcareproject.presentation.service.MeasurementMonitorService
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, MeasurementMonitorService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
