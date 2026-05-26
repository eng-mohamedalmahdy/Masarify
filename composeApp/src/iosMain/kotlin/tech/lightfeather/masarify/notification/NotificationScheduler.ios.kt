package tech.lightfeather.masarify.notification

import dev.brewkits.kmpworkmanager.background.domain.BackgroundTaskScheduler
import dev.brewkits.kmpworkmanager.background.domain.TaskTrigger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.lightfeather.domain.model.NotificationSettings

private const val REMINDER_WORKER_ID = "masarify_reminder_check"
private const val INTERVAL_THIRTY_MIN_MS = 30L * 60 * 1_000

actual object NotificationScheduler : KoinComponent {

    actual suspend fun scheduleAllReminders(settings: NotificationSettings) {
        if (!settings.isRemindersEnabled) {
            cancelAllReminders()
            return
        }
        val scheduler: BackgroundTaskScheduler by inject()
        scheduler.enqueue(
            id = REMINDER_WORKER_ID,
            trigger = TaskTrigger.Periodic(intervalMs = INTERVAL_THIRTY_MIN_MS),
            workerClassName = "ReminderCheckWorker",
        )
    }

    actual fun cancelAllReminders() {
        val scheduler: BackgroundTaskScheduler by inject()
        scheduler.cancel(REMINDER_WORKER_ID)
    }
}
