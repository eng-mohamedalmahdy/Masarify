package com.masarify

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class MasarifyApp : Application() {
    override fun onCreate() {
        Napier.base(DebugAntilog())
        super.onCreate()

    }
}