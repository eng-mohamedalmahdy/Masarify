package tech.lightfeather.masarify

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import io.github.vinceglb.filekit.manualFileKitCoreInitialization
import tech.lightfeather.designsystem.model.UiTransactionType
import tech.lightfeather.masarify.app.App
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.Route
import tech.lightfeather.masarify.navigation.routes.ResetPasswordRoute
import tech.lightfeather.masarify.navigation.routes.TransactionsRoute
import tech.lightfeather.masarify.navigation.routes.VerifyEmailRoute

class MainActivity : FragmentActivity() {
    // Stored after first composition so onNewIntent can navigate
    private var navigatorRef: Navigator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
        FileKit.init(this)
        FileKit.manualFileKitCoreInitialization(this)

        val deepLinkRoute = parseDeepLink(intent)
        setContent {
            App(
                pendingDeepLink = deepLinkRoute,
                onBackStackReady = { navigator -> navigatorRef = navigator },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val route = parseDeepLink(intent) ?: return
        navigatorRef?.navigate(route)
    }

    @Suppress("ReturnCount")
    private fun parseDeepLink(intent: Intent?): Route? {
        val uri = intent?.data?.takeIf { it.scheme == "masarify" } ?: return null
        return when (uri.host) {
            "transactions" -> {
                val transactionType =
                    when (uri.getQueryParameter("type")?.lowercase()) {
                        "income" -> UiTransactionType.INCOME
                        "transfer" -> UiTransactionType.TRANSFER
                        else -> UiTransactionType.EXPENSE
                    }
                TransactionsRoute(
                    openAddDialog = uri.getQueryParameter("openAddDialog") == "true",
                    transactionType = transactionType,
                    fromAccountId = uri.getQueryParameter("fromAccountId"),
                    categoryId = uri.getQueryParameter("categoryId"),
                )
            }

            "verify-email" -> {
                val token = uri.getQueryParameter("token") ?: return null
                VerifyEmailRoute(token = token)
            }

            "reset-password" -> {
                val token = uri.getQueryParameter("token") ?: return null
                ResetPasswordRoute(token = token)
            }

            else -> null
        }
    }
}

@Preview
@Composable
fun appAndroidPreview() {
    App()
}
