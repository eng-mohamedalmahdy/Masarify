package tech.lightfeather.masarify.notification

import androidx.compose.runtime.Composable

interface NotificationPermissionRequester {
    fun requestPermission(onResult: (granted: Boolean) -> Unit)

    fun isPermissionGranted(): Boolean
}

@Composable
expect fun rememberNotificationPermissionRequester(): NotificationPermissionRequester
