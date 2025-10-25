@file:Suppress("ForbiddenImport") // Window dimensions are platform-specific

package com.lightfeather.masarify

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.icerock.moko.resources.compose.stringResource
import com.lightfeather.masarify.app.App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

private val WINDOW_SIZE = DpSize(width = 1480.dp, height = 720.dp)

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(MR.strings.app_name),
            state = rememberWindowState(size = WINDOW_SIZE),
        ) {
            Napier.base(DebugAntilog())
            startKoin { }
            App()
        }
    }
