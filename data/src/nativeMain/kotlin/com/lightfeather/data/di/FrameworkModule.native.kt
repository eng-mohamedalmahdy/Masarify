package com.lightfeather.data.di

import com.lightfeather.data.local.database.DbFileAccessor
import com.lightfeather.data.local.database.NativeDbFileAccessor
import com.lightfeather.data.local.database.drivers.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val frameworkModule: Module =
    module {
        single<DriverFactory> { DriverFactory() }
        single<DbFileAccessor> { NativeDbFileAccessor() }
    }
