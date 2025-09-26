package com.lightfeather.masarify.template.transactionspane

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.runtime.Composable
import com.lightfeather.designsystem.model.UiTransactionFilter
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun TransactionsPane(
    title: String,
    filter: UiTransactionFilter = UiTransactionFilter.EMPTY,
    viewModel: TransactionsPanePageViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    topBarSupportingContent: @Composable () -> Unit = {},
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldPaneScope.TransactionsPaneAsDetail(
    title: String,
    filter: UiTransactionFilter = UiTransactionFilter.EMPTY,
    onBackClick: () -> Unit,
    supportingContent: @Composable () -> Unit,
) {
    AnimatedPane {
        TransactionsPane(title, filter = filter, onBackClick = onBackClick, topBarSupportingContent = supportingContent)
    }
}
