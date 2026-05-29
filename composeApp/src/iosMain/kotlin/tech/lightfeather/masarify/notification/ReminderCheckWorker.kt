package tech.lightfeather.masarify.notification

import dev.brewkits.kmpworkmanager.background.data.IosWorker
import dev.brewkits.kmpworkmanager.background.domain.WorkerEnvironment
import dev.brewkits.kmpworkmanager.background.domain.WorkerResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import tech.lightfeather.designsystem.MR
import tech.lightfeather.domain.model.ReminderType
import tech.lightfeather.domain.usecase.RecordReminderFired
import tech.lightfeather.domain.usecase.ShouldFireReminder

class ReminderCheckWorker :
    IosWorker,
    KoinComponent {
    private val shouldFireReminder: ShouldFireReminder by inject()
    private val recordReminderFired: RecordReminderFired by inject()

    override suspend fun doWork(
        input: String?,
        env: WorkerEnvironment,
    ): WorkerResult {
        ReminderType.entries.forEachIndexed { index, type ->
            if (shouldFireReminder(type)) {
                postNotification(type, index)
                recordReminderFired(type)
            }
        }
        return WorkerResult.Success(message = null)
    }

    private fun postNotification(
        type: ReminderType,
        index: Int,
    ) {
        val content = UNMutableNotificationContent()
        content.title = reminderTitle(type)
        content.body = reminderBody(type)
        val request =
            UNNotificationRequest.requestWithIdentifier(
                identifier = "masarify_reminder_${type.name}_$index",
                content = content,
                trigger = null,
            )
        UNUserNotificationCenter
            .currentNotificationCenter()
            .addNotificationRequest(request) { _ -> }
    }

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
}
