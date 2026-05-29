package tech.lightfeather.masarify.notification

import tech.lightfeather.designsystem.MR
import tech.lightfeather.domain.model.ReminderType
import tech.lightfeather.domain.usecase.RecordReminderFired
import tech.lightfeather.domain.usecase.ShouldFireReminder

@Suppress("UnusedParameter")
private fun jsIsNotificationGranted(): Boolean =
    js("typeof Notification !== 'undefined' && Notification.permission === 'granted'")

@Suppress("UnusedParameter")
private fun jsShowNotification(
    title: String,
    body: String,
): Unit = js("new Notification(title, { body: body })")

private fun reminderTitle(type: ReminderType): String =
    when (type) {
        ReminderType.DAILY_EXPENSE_LOG -> MR.strings.reminder_daily_expense_log_title.localized()
        ReminderType.WEEKLY_SUMMARY -> MR.strings.reminder_weekly_summary_title.localized()
        ReminderType.IDLE_RE_ENGAGEMENT -> MR.strings.reminder_idle_re_engagement_title.localized()
        ReminderType.MONTHLY_RECAP -> MR.strings.reminder_monthly_recap_title.localized()
    }

private fun reminderBody(type: ReminderType): String =
    when (type) {
        ReminderType.DAILY_EXPENSE_LOG -> MR.strings.reminder_daily_expense_log_body.localized()
        ReminderType.WEEKLY_SUMMARY -> MR.strings.reminder_weekly_summary_body.localized()
        ReminderType.IDLE_RE_ENGAGEMENT -> MR.strings.reminder_idle_re_engagement_body.localized()
        ReminderType.MONTHLY_RECAP -> MR.strings.reminder_monthly_recap_body.localized()
    }

actual object WebReminderChecker {
    actual fun checkAndFireIfNeeded(
        shouldFireReminder: ShouldFireReminder,
        recordReminderFired: RecordReminderFired,
    ) {
        if (!jsIsNotificationGranted()) return
        ReminderType.entries.forEach { type ->
            if (shouldFireReminder(type)) {
                jsShowNotification(title = reminderTitle(type), body = reminderBody(type))
                recordReminderFired(type)
            }
        }
    }
}
