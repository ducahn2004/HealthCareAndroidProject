package com.example.healthcareproject.presentation.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcareproject.R
import com.example.healthcareproject.presentation.util.AlertManagerUtil
import timber.log.Timber

class AlertActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.tag("AlertActivity").d("AlertActivity opened")

        // Show even on lock screen
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContentView(R.layout.activity_alert)

        val title = intent.getStringExtra("title") ?: "Alert"
        val message = intent.getStringExtra("message") ?: ""
        val phone = intent.getStringExtra("phoneNumber") ?: ""

        findViewById<TextView>(R.id.alertTitle).text = title
        findViewById<TextView>(R.id.alertMessage).text = message

        AlertManagerUtil.makeCall(this, phone)
    }
}
