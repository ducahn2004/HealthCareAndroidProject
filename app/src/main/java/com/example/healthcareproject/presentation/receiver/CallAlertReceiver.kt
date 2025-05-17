package com.example.healthcareproject.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthcareproject.presentation.util.AlertManagerUtil

class CallAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val phone = intent?.getStringExtra("phoneNumber") ?: return

        AlertManagerUtil.makeCall(context, phone)
    }
}
