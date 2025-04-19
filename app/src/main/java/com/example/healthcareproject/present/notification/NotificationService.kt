package com.example.healthcareproject.present.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.healthcareproject.R
import com.example.healthcareproject.present.notification.Notification
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationService : Service() {

    private lateinit var socket: Socket
    private val CHANNEL_ID = "HealthcareNotificationChannel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createForegroundNotification())
        setupSocket()
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
            socket = IO.socket("http://192.168.1.100:3000") // Thay YOUR_SERVER_URL bằng URL của server Socket
            //TODO REPLACE YOUR_SERVER_URL WITH SERVER SOCKET
            socket.on(Socket.EVENT_CONNECT) {
                // Kết nối thành công
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

                // Gửi thông báo tới UI thông qua LocalBroadcast
                val intent = Intent("NEW_NOTIFICATION")
                intent.putExtra("notification", notification)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

                // Hiển thị thông báo hệ thống
                showSystemNotification(notification)
            }.on(Socket.EVENT_DISCONNECT) {
                // Xử lý ngắt kết nối
            }
            socket.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSystemNotification(notification: Notification) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val androidNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setSmallIcon(notification.iconResId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(notification.id.toInt(), androidNotification)
    }

    override fun onDestroy() {
        socket.disconnect()
        super.onDestroy()
    }
}