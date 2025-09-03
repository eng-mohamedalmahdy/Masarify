package com.lightfeather.masarify

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.lightfeather.masarify.app.App
import kotlinx.browser.document
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        KoinApplication(
            application = {}
        ){
            App()
        }
    }
}