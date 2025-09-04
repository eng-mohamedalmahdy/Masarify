package com.lightfeather.masarify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import com.lightfeather.masarify.app.App
import kotlinx.browser.document
import kotlinx.browser.window
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    ComposeViewport(document.body!!) {

        startKoin{}
        App(
            onNavHostReady = { it.bindToBrowserNavigation() }
        )
    }
}



@Composable
fun rememberWindowSize(): IntSize {
    val sizeState = remember { mutableStateOf(IntSize(0, 0)) }

    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = {
            sizeState.value = IntSize(
                window.innerWidth,
                window.innerHeight
            )
        }

        // set initial size
        listener.invoke(Event("init"))

        window.addEventListener("resize", listener)
        onDispose {
            window.removeEventListener("resize", listener)
        }
    }

    return sizeState.value
}
