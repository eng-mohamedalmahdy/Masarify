package com.lightfeather.masarify.page.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.atoms.DashboardSectionHeader
import com.lightfeather.designsystem.component.molecules.BalanceOverviewCard
import com.lightfeather.designsystem.component.molecules.DashboardAccountCard
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.molecules.MonthSelector
import com.lightfeather.designsystem.component.molecules.button.PrimaryButton
import com.lightfeather.designsystem.component.organisms.SpendingAnalyticsCard
import com.lightfeather.designsystem.component.organisms.listitem.TransactionItem
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

/**
 * Dashboard page entry point with ViewModel integration
 */
@Composable
internal fun DashboardPage(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<DashboardPageViewModel>()
    val state by viewModel.state.collectAsState()

    DashboardPageContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

/**
 * Dashboard page content displaying all dashboard sections
 */
@Composable
internal fun DashboardPageContent(
    state: DashboardPageState,
    onIntent: (DashboardPageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(AppTheme.dimens.extraLarge))
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(AppTheme.dimens.default),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.large),
    ) {
        // Header with greeting
        Column {
            Text(
                text = stringResource(MR.strings.dashboard).orEmpty(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            if (state.userName.isNotEmpty()) {
                val greetingKey =
                    when (state.greeting) {
                        "good_morning" -> MR.strings.good_morning
                        "good_afternoon" -> MR.strings.good_afternoon
                        "good_evening" -> MR.strings.good_evening
                        else -> MR.strings.good_night
                    }
                Text(
                    text = "${stringResource(greetingKey)}, ${state.userName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = GREETING_ALPHA),
                )
            }
        }

        // Overview Section Header with Month Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(MR.strings.overview).orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            MonthSelector(
                selectedMonth = state.selectedMonth,
                onPreviousMonth = { onIntent(DashboardPageIntent.PreviousMonth) },
                onNextMonth = { onIntent(DashboardPageIntent.NextMonth) },
            )
        }

        // Balance Overview Card
        val availableCurrencies by state.availableCurrencies.collectAsState(initial = emptyList())
        val netBalance = calculateNetBalance(state.income, state.expense)

        BalanceOverviewCard(
            totalBalance = state.totalBalance,
            selectedCurrency = state.selectedCurrency,
            availableCurrencies = availableCurrencies,
            income = state.income,
            expense = state.expense,
            netBalance = netBalance,
            onCurrencySelect = { currency ->
                onIntent(DashboardPageIntent.SelectCurrency(currency))
            },
        )

        // Accounts Section
        DashboardSectionHeader(
            title = stringResource(MR.strings.accounts).orEmpty(),
            onSeeAllClick = { onIntent(DashboardPageIntent.NavigateToAccounts) },
        )

        val accountsList by state.accounts.collectAsState(initial = emptyList())
        if (accountsList.isEmpty()) {
            EmptyState(
                title = stringResource(MR.strings.no_accounts_title).orEmpty(),
                message = stringResource(MR.strings.no_accounts_message).orEmpty(),
                icon = Icons.Outlined.AccountBalance,
                action = {
                    PrimaryButton(
                        onClick = { onIntent(DashboardPageIntent.NavigateToAddAccount) },
                    ) {
                        Text(stringResource(MR.strings.add_account).orEmpty())
                    }
                },
                modifier = Modifier.height(AppTheme.dimens.massive * 2),
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
            ) {
                items(accountsList.take(state.displayedAccountsLimit)) { account ->
                    DashboardAccountCard(
                        account = account,
                        onClick = { onIntent(DashboardPageIntent.NavigateToAccount(account)) },
                    )
                }
            }
        }

        // Spending Analytics
        SpendingAnalyticsCard(
            spendingAnalytics = state.spendingAnalytics,
            isLoading = state.isAnalyticsLoading,
        )

        // Recent Transactions Section
        DashboardSectionHeader(
            title = stringResource(MR.strings.recent_transactions).orEmpty(),
            onSeeAllClick = { onIntent(DashboardPageIntent.NavigateToTransactions) },
        )

        val transactionsList by state.recentTransactions.collectAsState(initial = emptyList())
        if (transactionsList.isEmpty()) {
            EmptyState(
                title = stringResource(MR.strings.no_transactions_title).orEmpty(),
                message = stringResource(MR.strings.no_transactions_message).orEmpty(),
                icon = Icons.Outlined.Receipt,
                modifier = Modifier.height(AppTheme.dimens.massive * 3),
            )
        } else {
            Column {
                transactionsList.take(state.displayedTransactionsLimit).forEach { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { onIntent(DashboardPageIntent.NavigateToTransaction(transaction)) },
                    )
                }
            }
        }

        // Bottom spacing
        Spacer(modifier = Modifier.height(AppTheme.dimens.default))
    }
}

/**
 * Calculate net balance from income and expense strings
 */
private fun calculateNetBalance(
    income: String,
    expense: String,
): String {
    val incomeValue = income.toDoubleOrNull() ?: 0.0
    val expenseValue = expense.toDoubleOrNull() ?: 0.0
    val net = incomeValue - expenseValue
    return net.toString().replace(".0", "")
}

private const val GREETING_ALPHA = 0.7f

@Preview
@Composable
private fun DashboardPagePreview() {
    DashboardPageContent(
        state = DashboardPageState.dummy,
        onIntent = {},
    )
}
