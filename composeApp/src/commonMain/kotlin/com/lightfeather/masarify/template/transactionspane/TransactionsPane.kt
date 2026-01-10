package com.lightfeather.masarify.template.transactionspane

import androidx.compose.runtime.Composable
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.masarify.mappers.toTransactionFilter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Stateless transactions pane component
 * Platform-specific implementations handle Paging3 (Android/iOS) vs manual pagination (JVM/WasmJS)
 *
 * @param title Title for the pane
 * @param filter Current transaction filter
 * @param onBackClick Callback when back button is clicked
 * @param onTransactionClick Callback when a transaction is clicked
 * @param topBarSupportingContent Supporting content for top bar (title, filter button)
 */
@Composable
expect fun TransactionsPane(
    title: String,
    filter: UiTransactionFilter?,
    viewModel: TransactionsPaneViewModel = koinViewModel(parameters = { parametersOf(filter?.toTransactionFilter()) }),
    onBackClick: () -> Unit,
    onTransactionClick: (UiTransaction) -> Unit,
    topBarSupportingContent: @Composable () -> Unit = {},
)
