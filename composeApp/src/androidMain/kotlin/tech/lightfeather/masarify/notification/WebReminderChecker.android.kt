package tech.lightfeather.masarify.notification

import tech.lightfeather.domain.usecase.RecordReminderFired
import tech.lightfeather.domain.usecase.ShouldFireReminder

actual object WebReminderChecker {
    actual fun checkAndFireIfNeeded(
        shouldFireReminder: ShouldFireReminder,
        recordReminderFired: RecordReminderFired,
    ) = Unit
}
