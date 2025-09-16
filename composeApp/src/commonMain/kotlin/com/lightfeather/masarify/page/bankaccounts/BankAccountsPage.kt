package com.lightfeather.masarify.page.bankaccounts


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
//import androidx.compose.ui.backhandler.BackHandler
import com.lightfeather.designsystem.component.organisms.AccountsHeader
import com.lightfeather.designsystem.component.organisms.listitem.BankAccountItem
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.masarify.template.transactionspane.TransactionsPane
import com.lightfeather.masarify.template.transactionspane.TransactionsPaneAsDetail
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BankAccountsPage(
    viewModel: BankAccountsPageViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    BankAccountsPageContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun BankAccountsPageContent(
    state: BankAccountsPageState,
    onIntent: (BankAccountsPageIntent) -> Unit
) {
    val navigator = rememberListDetailPaneScaffoldNavigator()
    val coroutineScope = rememberCoroutineScope()
    val accounts by state.bankAccounts.collectAsState(emptyList())
    val userAccountsCurrencies by state.userAccountsCurrencies.collectAsState(emptyList())
    val defaultCurrency by state.defaultCurrency.collectAsState(null)
//    BackHandler(navigator.canNavigateBack()) {
//        coroutineScope.launch {
//            navigator.navigateBack()
//        }
//    }
    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AccountsListPane(
                accounts = accounts,
                userAccountsCurrencies = userAccountsCurrencies,
                totalAmountInSelectedOrDefaultCurrency = state.totalAmountInSelectedOrDefaultCurrency,
                defaultCurrency = defaultCurrency,
                selectedCurrency = state.selectedCurrency,
                onAccountClick = {
                    coroutineScope.launch {
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, it)
                    }
                },
                onUpdateAccount = { onIntent(BankAccountsPageIntent.UpdateBankAccount(it)) },
                onDeleteAccount = { onIntent(BankAccountsPageIntent.DeleteBankAccount(it)) },
                onCreateTransactionFromAccount = { onIntent(BankAccountsPageIntent.CreateTransactionInAccount(it)) },
                onTransferFromAccount = { onIntent(BankAccountsPageIntent.TransferFromAccount(it)) },
                onCurrencyClick = { onIntent(BankAccountsPageIntent.SelectCurrency(it)) }
            )
        },
        detailPane = { TransactionsPaneAsDetail() },
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ThreePaneScaffoldPaneScope.AccountsListPane(
    accounts: List<UiBankAccount>,
    userAccountsCurrencies: List<UiCurrency>,
    totalAmountInSelectedOrDefaultCurrency: String,
    defaultCurrency: UiCurrency?,
    selectedCurrency: UiCurrency?,
    onAccountClick: (UiBankAccount) -> Unit,
    onUpdateAccount: (UiBankAccount) -> Unit,
    onDeleteAccount: (UiBankAccount) -> Unit,
    onCreateTransactionFromAccount: (UiBankAccount) -> Unit,
    onTransferFromAccount: (UiBankAccount) -> Unit,
    onCurrencyClick: (UiCurrency?) -> Unit
) {
    AnimatedPane {
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                AccountsHeader(
                    userAccountsCurrencies = userAccountsCurrencies,
                    onCurrencyClick = onCurrencyClick,
                    totalAmountInSelectedOrDefaultCurrency = totalAmountInSelectedOrDefaultCurrency,
                    defaultCurrency = defaultCurrency,
                    selectedCurrency = selectedCurrency,
                    totalAccounts = accounts.size
                )
            }
            items(accounts) {
                BankAccountItem(
                    it,
                    onClick = { onAccountClick(it) },
                    onTransfer = { onTransferFromAccount(it) },
                    onEdit = { onUpdateAccount(it) },
                )
            }
        }
    }
}

@Preview
@Composable
fun BankAccountsScreenPreview() {
    AppTheme {
        BankAccountsPageContent(
            state = BankAccountsPageState(
                flowOf(List(10) { UiBankAccount.dummy }),
                flowOf(List(10) { UiCurrency.dummy }),
                flowOf(UiCurrency.dummy)
            ),
            onIntent = {}
        )
    }
}
