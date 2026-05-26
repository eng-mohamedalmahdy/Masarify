package tech.lightfeather.masarify

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.browser.document
import org.koin.core.context.startKoin
import org.w3c.dom.Worker
import tech.lightfeather.masarify.app.App
import tech.lightfeather.masarify.fcm.initWebPush

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {}
    Napier.base(DebugAntilog())
    initWebPush()
    ComposeViewport(document.body!!) {
        // Note: Browser history binding for Navigation 3 will be available in version 1.1.0
        // For now, navigation works but browser back/forward buttons are not integrated
        App()
    }
}

fun jsWorker(): Worker = js("""new Worker(new URL("./masarifyworker.worker.js", import.meta.url))""")
