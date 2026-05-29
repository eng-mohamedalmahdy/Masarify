package tech.lightfeather.masarify

import android.app.Application
import dev.brewkits.kmpworkmanager.KmpWorkManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import tech.lightfeather.domain.usecase.InitializeRevenueCatUseCase
import tech.lightfeather.masarify.di.coreModules
import tech.lightfeather.masarify.notification.AppAndroidWorkerFactory

class MasarifyApplication : Application() {
    private val initRevenueCat: InitializeRevenueCatUseCase by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MasarifyApplication)
            androidLogger()
            modules(coreModules)
        }
        initRevenueCat(BuildConfig.REVENUECAT_API_KEY, null)
        KmpWorkManager.initialize(
            context = this,
            workerFactory = AppAndroidWorkerFactory(),
        )
    }
}
