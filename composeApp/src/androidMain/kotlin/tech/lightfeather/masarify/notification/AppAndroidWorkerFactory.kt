package tech.lightfeather.masarify.notification

import dev.brewkits.kmpworkmanager.background.domain.AndroidWorker
import dev.brewkits.kmpworkmanager.background.domain.AndroidWorkerFactory

class AppAndroidWorkerFactory : AndroidWorkerFactory {
    override fun createWorker(workerClassName: String): AndroidWorker? = when (workerClassName) {
        "ReminderCheckWorker" -> ReminderCheckWorker()
        else -> null
    }
}
