package com.example.healthcareproject.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthcareproject.presentation.ui.activity.AlertActivity
import timber.log.Timber

class CallAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val phone = intent?.getStringExtra("phoneNumber") ?: return
        val title = intent.getStringExtra("title") ?: "Health Alert"
        val message = intent.getStringExtra("message") ?: "Critical measurement detected!"

        Timber.tag("CallAlertReceiver").d("Received alert broadcast for $phone")

        val activityIntent = Intent(context, AlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("phoneNumber", phone)
            putExtra("title", title)
            putExtra("message", message)
        }

        context.startActivity(activityIntent)
    }
}
