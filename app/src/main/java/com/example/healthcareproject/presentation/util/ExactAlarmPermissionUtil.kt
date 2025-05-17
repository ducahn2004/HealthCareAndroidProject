package com.example.healthcareproject.presentation.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.app.AlarmManager

object ExactAlarmPermissionUtil {

    fun checkAndRequestPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return false
            }
        }
        return true
    }
}
