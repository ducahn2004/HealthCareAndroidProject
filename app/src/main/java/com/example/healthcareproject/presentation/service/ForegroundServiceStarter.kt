package com.example.healthcareproject.presentation.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

object ForegroundServiceStarter {
    fun startMeasurementService(context: Context) {
        val intent = Intent(context, MeasurementMonitorService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopMeasurementService(context: Context) {
        val intent = Intent(context, MeasurementMonitorService::class.java)
        context.stopService(intent)
    }
}
