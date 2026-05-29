package tech.lightfeather.masarify.notification

import tech.lightfeather.domain.model.NotificationSettings

expect object NotificationScheduler {
    suspend fun scheduleAllReminders(settings: NotificationSettings)

    fun cancelAllReminders()
}
