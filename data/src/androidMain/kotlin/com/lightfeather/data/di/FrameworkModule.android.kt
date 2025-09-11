package com.lightfeather.data.di

import com.lightfeather.data.local.database.drivers.DriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val frameworkModule: Module =
    module {
        single<DriverFactory> { DriverFactory(androidContext()) }
    }
