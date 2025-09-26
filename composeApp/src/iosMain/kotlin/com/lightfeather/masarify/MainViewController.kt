@file:Suppress("ktlint:standard:function-naming")

package com.lightfeather.masarify

import androidx.compose.ui.window.ComposeUIViewController
import com.lightfeather.masarify.app.App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

fun InitApp() = startKoin {
    Napier.base(antilog = DebugAntilog())
}




fun MainViewController() = ComposeUIViewController { App() }
