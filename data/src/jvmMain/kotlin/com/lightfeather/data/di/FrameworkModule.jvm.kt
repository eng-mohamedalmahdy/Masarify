package com.lightfeather.data.di

import com.lightfeather.data.local.database.DbFileAccessor
import com.lightfeather.data.local.database.JvmDbFileAccessor
import com.lightfeather.data.local.database.drivers.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module
import java.nio.file.Paths

actual val frameworkModule: Module =
    module {
        val appPath =
            Paths
                .get("")
                .toAbsolutePath()
                .parent
                .toString()
        single { DriverFactory(appPath) }
        single<DbFileAccessor> { JvmDbFileAccessor(appPath) }
    }
