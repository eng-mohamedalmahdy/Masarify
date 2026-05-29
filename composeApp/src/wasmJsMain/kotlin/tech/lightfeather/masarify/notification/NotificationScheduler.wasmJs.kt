package tech.lightfeather.masarify.notification

import tech.lightfeather.domain.model.NotificationSettings

actual object NotificationScheduler {
    actual suspend fun scheduleAllReminders(settings: NotificationSettings) = Unit

    actual fun cancelAllReminders() = Unit
}
