package di

import data.local.DatabaseDriverFactory
import org.koin.dsl.module

actual fun databaseDriverFactoryModule() = module {
    single { DatabaseDriverFactory() }
}