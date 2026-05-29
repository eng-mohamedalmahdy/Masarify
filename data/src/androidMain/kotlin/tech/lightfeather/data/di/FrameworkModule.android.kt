package tech.lightfeather.data.di

import dev.brewkits.kmpworkmanager.background.data.NativeTaskScheduler
import dev.brewkits.kmpworkmanager.background.domain.BackgroundTaskScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import tech.lightfeather.data.RevenueCatSdkImpl
import tech.lightfeather.data.local.database.AndroidDbFileAccessor
import tech.lightfeather.data.local.database.DbFileAccessor
import tech.lightfeather.data.local.database.drivers.DriverFactory
import tech.lightfeather.domain.repository.RevenueCatSdk

actual val frameworkModule: Module =
    module {
        single<DriverFactory> { DriverFactory(androidContext()) }
        single<DbFileAccessor> { AndroidDbFileAccessor(androidContext()) }
        factory<BackgroundTaskScheduler> { NativeTaskScheduler(androidContext()) }
        single<RevenueCatSdk> { RevenueCatSdkImpl() }
    }
