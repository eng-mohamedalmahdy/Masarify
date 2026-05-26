@file:Suppress("ktlint:standard:function-naming")

package tech.lightfeather.masarify

import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import tech.lightfeather.designsystem.model.UiTransactionType
import tech.lightfeather.masarify.app.App
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.routes.ResetPasswordRoute
import tech.lightfeather.masarify.navigation.routes.TransactionsRoute
import tech.lightfeather.masarify.navigation.routes.VerifyEmailRoute

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

    @Suppress("CyclomaticComplexMethod", "ReturnCount")
    fun handleDeepLink(url: String) {
        val parts = url.split("?")
        if (parts.isEmpty()) return
        val path = parts[0]

        val params =
            if (parts.size > 1) {
                parts[1]
                    .split("&")
                    .associate { param ->
                        val (key, value) = param.split("=").let { it[0] to (it.getOrNull(1) ?: "") }
                        key to value
                    }
            } else {
                emptyMap()
            }

        val route =
            when {
                path.startsWith("masarify://transactions") -> {
                    val transactionType =
                        when (params["type"]?.lowercase()) {
                            "expense" -> UiTransactionType.EXPENSE
                            "income" -> UiTransactionType.INCOME
                            "transfer" -> UiTransactionType.TRANSFER
                            else -> UiTransactionType.EXPENSE
                        }
                    TransactionsRoute(
                        openAddDialog = params["openAddDialog"] == "true",
                        transactionType = transactionType,
                        fromAccountId = params["fromAccountId"],
                        categoryId = params["categoryId"],
                    )
                }

                path.startsWith("masarify://verify-email") -> {
                    val token = params["token"] ?: return
                    VerifyEmailRoute(token = token)
                }

                path.startsWith("masarify://reset-password") -> {
                    val token = params["token"] ?: return
                    ResetPasswordRoute(token = token)
                }

                else -> return
            }

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
