package tech.lightfeather.data.di

import org.koin.core.module.Module
import org.koin.dsl.module
import tech.lightfeather.data.local.database.DbFileAccessor
import tech.lightfeather.data.local.database.NativeDbFileAccessor
import tech.lightfeather.data.local.database.drivers.DriverFactory

actual val frameworkModule: Module =
    module {
        single<DriverFactory> { DriverFactory() }
        single<DbFileAccessor> { NativeDbFileAccessor() }
    }
