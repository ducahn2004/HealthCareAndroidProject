package com.example.healthcareproject.present.notification

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.healthcareproject.R
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log
import com.example.healthcareproject.present.MainActivity
class NotificationService : Service() {

    private lateinit var socket: Socket
    private val CHANNEL_ID = "HealthcareNotificationChannel"
    private val NOTIFICATION_ID = 1
    private lateinit var alertReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationService", "Service started")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createForegroundNotification())
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
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Healthcare App")
            .setContentText("Monitoring health notifications...")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

    private fun setupSocket() {
        try {
            socket = IO.socket("http://10.0.2.2:3000") // Update with your server IP
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
            }.on(Socket.EVENT_CONNECT_ERROR) {
                Log.e("SocketIO", "Connection error: ${it.toString()}")
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
                    Log.d("NotificationService", "Received heart rate alert: ${it.title}")
                    sendNotificationToUI(it)
                    showSystemNotification(it)
                }
            }
        }
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(alertReceiver, IntentFilter("HEART_RATE_ALERT"))
    }

    private fun sendNotificationToUI(notification: Notification) {
        val intent = Intent("NEW_NOTIFICATION")
        intent.putExtra("notification", notification)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun showSystemNotification(notification: Notification) {
        val notificationManager = getSystemService(NotificationManager::class.java)

        // Create a PendingIntent to open the app
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigate_to", "heart_rate") // Optional: Add extra to navigate to HeartRateFragment
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
            .setContentIntent(pendingIntent) // Add PendingIntent
            .setAutoCancel(true) // Dismiss notification when tapped
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