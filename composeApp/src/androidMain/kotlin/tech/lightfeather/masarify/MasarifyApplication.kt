package tech.lightfeather.masarify

import android.app.Application
import dev.brewkits.kmpworkmanager.KmpWorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import tech.lightfeather.masarify.di.coreModules
import tech.lightfeather.masarify.notification.AppAndroidWorkerFactory

class MasarifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MasarifyApplication)
            androidLogger()
            modules(coreModules)
        }
        KmpWorkManager.initialize(
            context = this,
            workerFactory = AppAndroidWorkerFactory(),
        )
    }
}
