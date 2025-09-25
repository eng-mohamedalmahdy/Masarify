package com.lightfeather.masarify

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.lightfeather.masarify.app.App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Masarify",
            resizable = false,
            state = rememberWindowState(width = 1280.dp, height = 720.dp),
        ) {
            Napier.base(DebugAntilog())
            startKoin { }
            App()
        }
    }
