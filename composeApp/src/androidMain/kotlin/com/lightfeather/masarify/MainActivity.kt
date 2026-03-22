package com.lightfeather.masarify

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.masarify.app.App
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.TransactionsRoute
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import io.github.vinceglb.filekit.manualFileKitCoreInitialization

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

    private fun parseDeepLink(intent: Intent?): TransactionsRoute? {
        val uri =
            intent?.data?.takeIf { it.scheme == "masarify" && it.host == "transactions" }
                ?: return null

        val transactionType =
            when (uri.getQueryParameter("type")?.lowercase()) {
                "income" -> UiTransactionType.INCOME
                "transfer" -> UiTransactionType.TRANSFER
                else -> UiTransactionType.EXPENSE
            }

        return TransactionsRoute(
            openAddDialog = uri.getQueryParameter("openAddDialog") == "true",
            transactionType = transactionType,
            fromAccountId = uri.getQueryParameter("fromAccountId"),
            categoryId = uri.getQueryParameter("categoryId"),
        )
    }
}

@Preview
@Composable
fun appAndroidPreview() {
    App()
}
