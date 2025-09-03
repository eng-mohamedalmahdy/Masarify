package com.lightfeather.masarify

import androidx.compose.ui.window.ComposeUIViewController
import com.lightfeather.masarify.app.App
import org.koin.core.context.startKoin


fun InitApp() {
    startKoin {

    }

}

fun MainViewController() = ComposeUIViewController { App() }