package com.example.healthcareproject.presentation.ui.activity

import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.healthcareproject.R
import timber.log.Timber

class ReminderActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.3f)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            attributes.gravity = Gravity.TOP
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT
            setBackgroundDrawableResource(android.R.color.transparent)

            decorView.setPadding(0, 0, 0, 0)
        }

        setContentView(R.layout.activity_reminder)

        val title = intent.getStringExtra("title") ?: "Reminder"
        val message = intent.getStringExtra("message") ?: ""

        findViewById<TextView>(R.id.reminderTitle).text = title
        findViewById<TextView>(R.id.reminderMessage).text = message

        findViewById<Button>(R.id.btnStopAlarm).setOnClickListener {
            stopAlarmSound()
            finish()
        }

        playSystemAlarmSound()
    }

    private fun playSystemAlarmSound() {
        try {
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@ReminderActivity, alarmUri)
                isLooping = true
                setOnPreparedListener { it.start() }
                prepareAsync()
            }

            Timber.tag("ReminderActivity").d("System alarm sound started")
        } catch (e: Exception) {
            Timber.tag("ReminderActivity").e(e, "Error playing system alarm sound")
        }
    }

    private fun stopAlarmSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        Timber.tag("ReminderActivity").d("System alarm sound stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarmSound()
    }

    override fun onResume() {
        super.onResume()
        // Đảm bảo cửa sổ có thể tương tác
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    override fun onPause() {
        super.onPause()
        // Không thêm cờ không tương tác khi tạm dừng
    }
}