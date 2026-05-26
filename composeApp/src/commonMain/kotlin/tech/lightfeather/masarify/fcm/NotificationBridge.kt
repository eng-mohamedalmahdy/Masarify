package tech.lightfeather.masarify.fcm

expect object NotificationBridge {
    fun onNewFcmToken(
        token: String,
        platform: String,
    )

    fun onSyncRequested()
}
