package tech.lightfeather.masarify.page.notificationsettings

import tech.lightfeather.domain.model.ReminderType

internal sealed interface NotificationSettingsPageIntent {
    data object LoadData : NotificationSettingsPageIntent

    data class ToggleMasterReminders(val enabled: Boolean) : NotificationSettingsPageIntent

    data class ToggleReminderType(val type: ReminderType, val enabled: Boolean) : NotificationSettingsPageIntent

    data class SetDailyReminderTime(val minutes: Int) : NotificationSettingsPageIntent

    data class UpdatePermissionState(val granted: Boolean) : NotificationSettingsPageIntent
}
