@file:Suppress("ktlint:standard:function-naming")

package com.lightfeather.masarify

import androidx.compose.ui.window.ComposeUIViewController
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.masarify.app.App
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.TransactionsRoute
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin

fun InitApp() =
    startKoin {
        Napier.base(antilog = DebugAntilog())
    }

object DeeplinkBridge {
    private var navigator: Navigator? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    internal fun setNavigator(navigator: Navigator) {
        this.navigator = navigator
    }

    fun handleDeepLink(url: String) {
        val uri = url.split("?")
        if (uri.isEmpty()) return
        val path = uri[0]
        if (!path.startsWith("masarify://transactions")) return

        val params =
            if (uri.size > 1) {
                uri[1]
                    .split("&")
                    .associate { param ->
                        val (key, value) = param.split("=").let { it[0] to (it.getOrNull(1) ?: "") }
                        key to value
                    }
            } else {
                emptyMap()
            }

        val openAddDialog = params["openAddDialog"] == "true"
        val transactionType =
            when (params["type"]?.lowercase()) {
                "expense" -> UiTransactionType.EXPENSE
                "income" -> UiTransactionType.INCOME
                "transfer" -> UiTransactionType.TRANSFER
                else -> UiTransactionType.EXPENSE
            }
        val categoryId = params["categoryId"]
        val fromAccountId = params["fromAccountId"]

        val route =
            TransactionsRoute(
                openAddDialog = openAddDialog,
                transactionType = transactionType,
                fromAccountId = fromAccountId,
                categoryId = categoryId,
            )

        scope.launch {
            navigator?.navigate(route)
        }
    }
}

fun MainViewController() =
    ComposeUIViewController {
        App(
            onBackStackReady = { nav ->
                DeeplinkBridge.setNavigator(nav)
            },
        )
    }
