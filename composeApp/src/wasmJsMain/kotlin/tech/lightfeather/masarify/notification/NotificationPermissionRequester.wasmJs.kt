package tech.lightfeather.masarify.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Suppress("UnusedParameter")
private fun jsIsNotificationGranted(): Boolean =
    js("typeof Notification !== 'undefined' && Notification.permission === 'granted'")

@Suppress("UnusedParameter")
private fun jsRequestNotificationPermission(): Unit =
    js("if (typeof Notification !== 'undefined') Notification.requestPermission()")

private object WebNotificationPermissionRequester : NotificationPermissionRequester {
    override fun requestPermission(onResult: (Boolean) -> Unit) {
        jsRequestNotificationPermission()
        onResult(jsIsNotificationGranted())
    }

    override fun isPermissionGranted(): Boolean = jsIsNotificationGranted()
}

@Composable
actual fun rememberNotificationPermissionRequester(): NotificationPermissionRequester =
    remember { WebNotificationPermissionRequester }
