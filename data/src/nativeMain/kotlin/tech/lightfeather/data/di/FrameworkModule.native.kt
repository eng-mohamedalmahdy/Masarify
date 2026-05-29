package tech.lightfeather.data.di

import dev.brewkits.kmpworkmanager.background.data.NativeTaskScheduler
import dev.brewkits.kmpworkmanager.background.domain.BackgroundTaskScheduler
import org.koin.core.module.Module
import org.koin.dsl.module
import tech.lightfeather.data.RevenueCatSdkImpl
import tech.lightfeather.data.local.database.DbFileAccessor
import tech.lightfeather.data.local.database.NativeDbFileAccessor
import tech.lightfeather.data.local.database.drivers.DriverFactory
import tech.lightfeather.domain.repository.RevenueCatSdk

actual val frameworkModule: Module =
    module {
        single<DriverFactory> { DriverFactory() }
        single<DbFileAccessor> { NativeDbFileAccessor() }
        factory<BackgroundTaskScheduler> {
            NativeTaskScheduler()
        }
        single<RevenueCatSdk> { RevenueCatSdkImpl() }
    }
