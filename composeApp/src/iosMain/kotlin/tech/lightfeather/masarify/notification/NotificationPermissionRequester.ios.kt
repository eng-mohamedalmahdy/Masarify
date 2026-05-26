package tech.lightfeather.masarify.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

private class IosNotificationPermissionRequester : NotificationPermissionRequester {
    private var cachedGranted = false

    init {
        UNUserNotificationCenter.currentNotificationCenter()
            .getNotificationSettingsWithCompletionHandler { settings ->
                cachedGranted = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
            }
    }

    override fun requestPermission(onResult: (Boolean) -> Unit) {
        val options = UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound
        UNUserNotificationCenter.currentNotificationCenter()
            .requestAuthorizationWithOptions(options) { granted, _ ->
                cachedGranted = granted
                onResult(granted)
            }
    }

    override fun isPermissionGranted(): Boolean = cachedGranted
}

@Composable
actual fun rememberNotificationPermissionRequester(): NotificationPermissionRequester =
    remember { IosNotificationPermissionRequester() }
