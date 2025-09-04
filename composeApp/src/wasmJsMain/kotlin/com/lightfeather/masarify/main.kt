package com.lightfeather.masarify

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import com.lightfeather.masarify.app.App
import kotlinx.browser.document
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        startKoin{}
        App(
            onNavHostReady = { it.bindToBrowserNavigation() }
        )
    }
}