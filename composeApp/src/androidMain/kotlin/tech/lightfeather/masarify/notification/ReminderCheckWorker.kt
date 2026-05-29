package tech.lightfeather.masarify.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.brewkits.kmpworkmanager.background.domain.AndroidWorker
import dev.brewkits.kmpworkmanager.background.domain.WorkerEnvironment
import dev.brewkits.kmpworkmanager.background.domain.WorkerResult
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.lightfeather.designsystem.MR
import tech.lightfeather.domain.model.ReminderType
import tech.lightfeather.domain.usecase.RecordReminderFired
import tech.lightfeather.domain.usecase.ShouldFireReminder

private const val REMINDER_CHANNEL_ID = "masarify_reminders"
private const val NOTIFICATION_BASE_ID = 2000

class ReminderCheckWorker :
    AndroidWorker,
    KoinComponent {
    private val shouldFireReminder: ShouldFireReminder by inject()
    private val recordReminderFired: RecordReminderFired by inject()
    private val context: Context by inject()

    override suspend fun doWork(
        input: String?,
        env: WorkerEnvironment,
    ): WorkerResult {
        ensureNotificationChannel()
        ReminderType.entries.forEachIndexed { index, type ->
            if (shouldFireReminder(type)) {
                postNotification(type, NOTIFICATION_BASE_ID + index)
                recordReminderFired(type)
            }
        }
        return WorkerResult.Success(message = null)
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel =
            NotificationChannel(
                REMINDER_CHANNEL_ID,
                context.getString(MR.strings.notification_settings.resourceId),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun postNotification(
        type: ReminderType,
        notificationId: Int,
    ) {
        val titleRes =
            when (type) {
                ReminderType.DAILY_EXPENSE_LOG -> MR.strings.reminder_daily_expense_log_title
                ReminderType.WEEKLY_SUMMARY -> MR.strings.reminder_weekly_summary_title
                ReminderType.IDLE_RE_ENGAGEMENT -> MR.strings.reminder_idle_re_engagement_title
                ReminderType.MONTHLY_RECAP -> MR.strings.reminder_monthly_recap_title
            }
        val bodyRes =
            when (type) {
                ReminderType.DAILY_EXPENSE_LOG -> MR.strings.reminder_daily_expense_log_body
                ReminderType.WEEKLY_SUMMARY -> MR.strings.reminder_weekly_summary_body
                ReminderType.IDLE_RE_ENGAGEMENT -> MR.strings.reminder_idle_re_engagement_body
                ReminderType.MONTHLY_RECAP -> MR.strings.reminder_monthly_recap_body
            }
        val notification =
            NotificationCompat
                .Builder(context, REMINDER_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setContentTitle(context.getString(titleRes.resourceId))
                .setContentText(context.getString(bodyRes.resourceId))
                .setAutoCancel(true)
                .build()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
