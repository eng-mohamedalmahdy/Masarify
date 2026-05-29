package tech.lightfeather.data.di

import org.koin.core.module.Module
import org.koin.dsl.module
import tech.lightfeather.data.RevenueCatSdkImpl
import tech.lightfeather.data.local.database.DbFileAccessor
import tech.lightfeather.data.local.database.WasmDbFileAccessor
import tech.lightfeather.data.local.database.drivers.DriverFactory
import tech.lightfeather.domain.repository.RevenueCatSdk

actual val frameworkModule: Module =
    module {
        single<DriverFactory> { DriverFactory() }
        single<DbFileAccessor> { WasmDbFileAccessor() }
        single<RevenueCatSdk> { RevenueCatSdkImpl() }
    }
