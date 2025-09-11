package com.lightfeather.data.di

import com.lightfeather.data.local.database.drivers.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module
import java.nio.file.Paths

actual val frameworkModule: Module =
    module {
        single {
            DriverFactory(
                Paths
                    .get("")
                    .toAbsolutePath()
                    .parent
                    .toString(),
            )
        }
    }
