package com.lightfeather.masarify

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lightfeather.masarify.app.App
import org.koin.core.context.startKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Masarify",
    ) {

        startKoin {  }
        App()
    }
}