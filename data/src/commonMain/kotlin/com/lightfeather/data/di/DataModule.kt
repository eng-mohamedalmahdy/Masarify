package com.lightfeather.data.di


import com.lightfeather.data.local.AppPreferences
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val dataModule = module {
    single<SharedDatabase> { SharedDatabase(get()) }
    single<AppPreferences> { AppPreferences(get()) }
    single<Settings> { Settings() }
}