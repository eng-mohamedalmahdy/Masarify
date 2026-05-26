package tech.lightfeather.masarify.notification

import tech.lightfeather.domain.usecase.RecordReminderFired
import tech.lightfeather.domain.usecase.ShouldFireReminder

expect object WebReminderChecker {
    fun checkAndFireIfNeeded(
        shouldFireReminder: ShouldFireReminder,
        recordReminderFired: RecordReminderFired,
    )
}
