package com.example.healthcareproject.present.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.healthcareproject.BuildConfig
import com.example.healthcareproject.R
import com.example.healthcareproject.present.MainActivity
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.EngineIOException
import org.json.JSONObject
import android.Manifest
import android.util.Log
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationService : Service() {

    private lateinit var socket: Socket
    private val CHANNEL_ID = "HealthcareNotificationChannel"
    private val FOREGROUND_NOTIFICATION_ID = 1
    private lateinit var alertReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationService", "Service started")
        createNotificationChannel()
        startForeground(FOREGROUND_NOTIFICATION_ID, createForegroundNotification())
        setupSocket()
        setupAlertReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Healthcare Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for healthcare app notifications"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Healthcare App")
            .setContentText("Monitoring health notifications...")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    private fun setupSocket() {
        try {
            val options = IO.Options().apply {
                reconnection = true
                reconnectionAttempts = 10
                reconnectionDelay = 1000
                timeout = 20000
            }
            socket = IO.socket(BuildConfig.SOCKET_URL, options)
            socket.on(Socket.EVENT_CONNECT) {
                Log.d("SocketIO", "Connected to server")
            }.on("newNotification") { args ->
                val data = args[0] as JSONObject
                val title = data.getString("title")
                val message = data.getString("message")
                val iconResId = when (title) {
                    "HEART RATE ALERT" -> R.drawable.ic_heart_rate
                    "ECG ALERT" -> R.drawable.ic_ecg
                    "OXYGEN LEVEL ALERT" -> R.drawable.ic_oxygen
                    "UPDATE WEIGHT" -> R.drawable.ic_weight
                    else -> R.drawable.ic_notification
                }
                val time = SimpleDateFormat("hh:mma", Locale.getDefault()).format(Date())

                val notification = Notification(
                    id = System.currentTimeMillis(),
                    title = title,
                    message = message,
                    time = time,
                    iconResId = iconResId
                )

                sendNotificationToUI(notification)
                showSystemNotification(notification)
            }.on(Socket.EVENT_CONNECT_ERROR) { args ->
                val error = args[0] as? EngineIOException
                Log.e("SocketIO", "Connection error: ${error?.message}")
            }.on(Socket.EVENT_DISCONNECT) {
                Log.d("SocketIO", "Disconnected from server")
            }
            socket.connect()
        } catch (e: Exception) {
            Log.e("SocketIO", "Socket setup error: ${e.message}")
        }
    }

    private fun setupAlertReceiver() {
        alertReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent?.getParcelableExtra("notification", Notification::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent?.getParcelableExtra("notification")
                }
                notification?.let {
                    Log.d("NotificationService", "Received alert: ${it.title}")
                    sendNotificationToUI(it)
                    showSystemNotification(it)
                }
            }
        }
        LocalBroadcastManager.getInstance(this).apply {
            registerReceiver(alertReceiver, IntentFilter("HEART_RATE_ALERT"))
            registerReceiver(alertReceiver, IntentFilter("OXYGEN_LEVEL_ALERT"))
        }
    }

    private fun sendNotificationToUI(notification: Notification) {
        val intent = Intent("NEW_NOTIFICATION")
        intent.putExtra("notification", notification)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun showSystemNotification(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.w("NotificationService", "Notification permission not granted")
            return
        }

        val notificationManager = getSystemService(NotificationManager::class.java)

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", when (notification.title) {
                "HEART RATE ALERT" -> "heart_rate"
                "OXYGEN LEVEL ALERT" -> "oxygen"
                else -> ""
            })
        }
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val androidNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setSmallIcon(notification.iconResId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notification.id.toInt(), androidNotification)
    }

    override fun onDestroy() {
        Log.d("NotificationService", "Service destroyed")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(alertReceiver)
        socket.disconnect()
        super.onDestroy()
    }
}