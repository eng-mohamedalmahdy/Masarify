package com.masarify

import android.app.Application
import android.content.Context
import di.databaseDriverFactoryModule
import di.initKoinAndroid
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class MasarifyApp : Application() {
    override fun onCreate() {
        Napier.base(DebugAntilog())
        super.onCreate()
        initKoinAndroid(
            listOf(
                module {
                    single<Context> { this@MasarifyApp }
                }
            )
        )
    }
}