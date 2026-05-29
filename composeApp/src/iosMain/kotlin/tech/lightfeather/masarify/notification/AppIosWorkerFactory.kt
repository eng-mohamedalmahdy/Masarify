package tech.lightfeather.masarify.notification

import dev.brewkits.kmpworkmanager.background.data.IosWorker
import dev.brewkits.kmpworkmanager.background.data.IosWorkerFactory

class AppIosWorkerFactory : IosWorkerFactory {
    override fun createWorker(workerClassName: String): IosWorker? =
        when (workerClassName) {
            "ReminderCheckWorker" -> ReminderCheckWorker()
            else -> null
        }
}
