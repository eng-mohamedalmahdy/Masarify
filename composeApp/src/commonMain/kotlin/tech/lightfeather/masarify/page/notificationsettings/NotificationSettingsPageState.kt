package tech.lightfeather.masarify.page.notificationsettings

import tech.lightfeather.domain.model.NotificationSettings

internal data class NotificationSettingsPageState(
    val settings: NotificationSettings = NotificationSettings(),
    val isPermissionGranted: Boolean = false,
)
