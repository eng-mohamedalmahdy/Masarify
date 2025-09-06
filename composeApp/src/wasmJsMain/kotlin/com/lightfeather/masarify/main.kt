package com.lightfeather.masarify

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import com.lightfeather.masarify.app.App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import org.koin.core.context.startKoin
import org.w3c.dom.Worker

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    ComposeViewport(document.body!!) {

        startKoin{}
        Napier.base(DebugAntilog())
        App(
            onNavHostReady = { it.bindToBrowserNavigation() }
        )
    }
}


fun jsWorker(): Worker =
    js("""new Worker(new URL("./masarifyworker.worker.js", import.meta.url))""")