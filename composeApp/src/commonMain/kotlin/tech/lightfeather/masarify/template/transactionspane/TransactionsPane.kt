package tech.lightfeather.masarify.template.transactionspane

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import tech.lightfeather.designsystem.model.UiTransaction
import tech.lightfeather.designsystem.model.UiTransactionFilter
import tech.lightfeather.masarify.mappers.toTransactionFilter

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
    onAddClick: () -> Unit,
    onBackClick: () -> Unit,
    onTransactionClick: (UiTransaction) -> Unit,
    topBarSupportingContent: @Composable () -> Unit = {},
)
