package com.lightfeather.masarify.page.bankaccounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.RectangleShape
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.organisms.AccountsHeader
import com.lightfeather.designsystem.component.organisms.listitem.BankAccountItem
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.designsystem.model.uiTransactionFilter
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.masarify.template.transactionspane.TransactionsPaneAsDetail
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BankAccountsPage(viewModel: BankAccountsPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    BankAccountsPageContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun BankAccountsPageContent(
    state: BankAccountsPageState,
    onIntent: (BankAccountsPageIntent) -> Unit,
) {
    val navigator = rememberListDetailPaneScaffoldNavigator()
    val coroutineScope = rememberCoroutineScope()
    val accounts by state.bankAccounts.collectAsState(emptyList())
    val userAccountsCurrencies by state.userAccountsCurrencies.collectAsState(emptyList())
    val defaultCurrency by state.defaultCurrency.collectAsState(null)
    val paneFilter by remember(state.selectedAccount) {
        derivedStateOf {
            if (state.selectedAccount != null) {
                uiTransactionFilter { accountIn(state.selectedAccount) }
            } else {
                UiTransactionFilter.EMPTY
            }
        }
    }
    BackHandler(navigator.canNavigateBack()) {
        coroutineScope.launch {
            navigator.navigateBack()
        }
    }
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
                        onIntent(BankAccountsPageIntent.SelectAccount(it))
                    }
                },
                onUpdateAccount = { onIntent(BankAccountsPageIntent.UpdateBankAccount(it)) },
                onDeleteAccount = { onIntent(BankAccountsPageIntent.DeleteBankAccount(it)) },
                onCreateTransactionFromAccount = { onIntent(BankAccountsPageIntent.CreateTransactionInAccount(it)) },
                onTransferFromAccount = { onIntent(BankAccountsPageIntent.TransferFromAccount(it)) },
                onCurrencyClick = { onIntent(BankAccountsPageIntent.SelectCurrency(it)) },
            )
        },
        detailPane = {
            if (state.selectedAccount != null) {
                TransactionsPaneAsDetail(paneFilter)
            } else {
                EmptyState(
                    title = stringResource(MR.strings.no_account_selected_title),
                    message = stringResource(MR.strings.no_account_selected_message),
                    icon = Icons.Outlined.AccountBalance,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
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
    onCurrencyClick: (UiCurrency?) -> Unit,
) {
    AnimatedPane {
        if (accounts.isEmpty()) {
            EmptyState(
                title = stringResource(MR.strings.no_accounts_title),
                message = stringResource(MR.strings.no_accounts_message),
                icon = Icons.Outlined.AccountBalance,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = AppTheme.dimens.large),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.hairline),
            ) {
                // Wealth Summary Header
                item {
                    AccountsHeader(
                        userAccountsCurrencies = userAccountsCurrencies,
                        onCurrencyClick = onCurrencyClick,
                        totalAmountInSelectedOrDefaultCurrency = totalAmountInSelectedOrDefaultCurrency,
                        defaultCurrency = defaultCurrency,
                        selectedCurrency = selectedCurrency,
                        totalAccounts = accounts.size,
                        shape = RectangleShape,
                    )
                }

                // Accounts Section Header
                item {
                    Text(
                        text = stringResource(MR.strings.your_accounts),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = AppTheme.dimens.default,
                                    vertical = AppTheme.dimens.medium,
                                ),
                    )
                }

                // Bank Account Items
                items(
                    items = accounts,
                    key = { it.id },
                ) { account ->
                    BankAccountItem(
                        account,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppTheme.dimens.default),
                        onClick = { onAccountClick(account) },
                        onTransfer = { onTransferFromAccount(account) },
                        onEdit = { onUpdateAccount(account) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BankAccountsScreenPreview() {
    AppTheme {
        BankAccountsPageContent(
            state =
                BankAccountsPageState(
                    flowOf(List(10) { UiBankAccount.dummy }),
                    flowOf(List(10) { UiCurrency.dummy }),
                    flowOf(UiCurrency.dummy),
                ),
            onIntent = {},
        )
    }
}
