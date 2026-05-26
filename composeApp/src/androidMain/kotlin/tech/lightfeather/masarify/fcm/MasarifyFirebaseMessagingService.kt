package tech.lightfeather.masarify.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val NOTIFICATION_CHANNEL_ID = "masarify_channel"
private const val NOTIFICATION_CHANNEL_NAME = "Masarify"

class MasarifyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        NotificationBridge.onNewFcmToken(token, "android")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        when (message.data["type"]) {
            "sync" -> NotificationBridge.onSyncRequested()
            else -> showNotification(message.notification?.title, message.notification?.body)
        }
    }

    private fun showNotification(
        title: String?,
        body: String?,
    ) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            manager.createNotificationChannel(channel)
        }

        val notification =
            NotificationCompat
                .Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
