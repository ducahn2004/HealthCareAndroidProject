package com.example.healthcareproject.domain.usecase.alert

import android.content.Context
import androidx.core.content.edit

object AlertThrottleManager {
    private const val THROTTLE_DURATION_MS = 3 * 60 * 1000L

    fun shouldTriggerAlert(context: Context): Boolean {
        val prefs = context.getSharedPreferences("alert_prefs", Context.MODE_PRIVATE)
        val lastTime = prefs.getLong("last_alert_time", 0L)
        val currentTime = System.currentTimeMillis()

        return if (currentTime - lastTime >= THROTTLE_DURATION_MS) {
            prefs.edit { putLong("last_alert_time", currentTime) }
            true
        } else {
            false
        }
    }

}