package com.example.healthcareproject.presentation.ui.activity

import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
            addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            setShowWhenLocked(true)
            setTurnScreenOn(true)
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
}
