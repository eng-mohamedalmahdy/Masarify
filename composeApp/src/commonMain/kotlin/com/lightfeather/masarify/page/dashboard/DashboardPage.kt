package com.lightfeather.masarify.page.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.atoms.DashboardSectionHeader
import com.lightfeather.designsystem.component.molecules.BalanceOverviewCard
import com.lightfeather.designsystem.component.molecules.DashboardAccountCard
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.molecules.MonthSelector
import com.lightfeather.designsystem.component.molecules.button.PrimaryButton
import com.lightfeather.designsystem.component.organisms.SpendingAnalyticsCard
import com.lightfeather.designsystem.component.organisms.TransactionDetailView
import com.lightfeather.designsystem.component.organisms.TransactionTimelineColumn
import com.lightfeather.designsystem.component.organisms.dialog.AddEditTransactionDialog
import com.lightfeather.designsystem.component.organisms.dialog.BiometricSuggestionDialog
import com.lightfeather.designsystem.component.organisms.dialog.FixBalanceDialog
import com.lightfeather.designsystem.component.organisms.dialog.StartOverDialog
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.uiTransactionFilter
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.stringResource
import com.lightfeather.masarify.auth.rememberBiometricAuthenticator
import com.lightfeather.masarify.mappers.toTransaction
import com.lightfeather.masarify.mappers.toUiTransactionDetails
import com.lightfeather.masarify.navigation.Display
import com.lightfeather.masarify.navigation.LocalNavigator
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.template.transactionspane.TransactionsPane
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

/**
 * Dashboard page entry point with ViewModel integration
 */
@Composable
internal fun DashboardPage(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<DashboardPageViewModel>()
    DashboardPageContent(
        viewModel = viewModel,
        modifier = modifier,
    )
}

// Composable UI function with navigation and detail pane management - length is acceptable for UI composition
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun DashboardPageContent(
    viewModel: DashboardPageViewModel,
    modifier: Modifier = Modifier,
    navigator: Navigator = LocalNavigator.current,
) {
    // Create scoped list-detail navigator
    val listDetailNav = navigator.forListDetail(DashboardList)

    // Create list-detail scene strategy for adaptive layout
    val listDetailStrategy =
        rememberListDetailSceneStrategy<NavKey>(
            directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo(true)),
            adaptStrategies =
                ListDetailPaneScaffoldDefaults.adaptStrategies(
                    detailPaneAdaptStrategy = AdaptStrategy.Reflow(ThreePaneScaffoldRole.Primary),
                    listPaneAdaptStrategy = AdaptStrategy.Reflow(ThreePaneScaffoldRole.Secondary),
                    extraPaneAdaptStrategy = AdaptStrategy.Reflow(ThreePaneScaffoldRole.Tertiary),
                ),
        )

    var isFabExpanded by remember { mutableStateOf(false) }

    val onIntent = viewModel::onIntent

    // Check biometric suggestion once on launch
    val biometricAuthenticator = rememberBiometricAuthenticator()
    LaunchedEffect(Unit) {
        onIntent(DashboardPageIntent.CheckBiometricSuggestion(biometricAuthenticator.isAvailable()))
    }

    // Outer-layer subscriptions for dialogs
    val state by viewModel.state.collectAsState()
    val accounts by state.accounts.collectAsState(emptyList())

    Box(modifier = modifier.fillMaxSize()) {
        listDetailNav.Display(
            sceneStrategy = listDetailStrategy,
            modifier = Modifier.fillMaxSize(),
        ) {
            // List pane entry - main dashboard
            entry<DashboardList>(
                metadata =
                    ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            EmptyState(
                                title = stringResource(MR.strings.no_account_selected_title).orEmpty(),
                                message = stringResource(MR.strings.no_account_selected_message).orEmpty(),
                                icon = Icons.Outlined.AccountBalance,
                                modifier = Modifier.fillMaxSize(),
                            )
                        },
                    ),
            ) {
                // Per-entry live subscription — recomposes this entry directly when state changes
                val entryState by viewModel.state.collectAsState()
                DashboardListPane(
                    state = entryState,
                    onIntent = onIntent,
                    onAccountClick = { account ->
                        listDetailNav.navigateToDetail(ViewAccountTransactions(account.id))
                        onIntent(DashboardPageIntent.SelectAccount(account))
                    },
                    onTransactionClick = { transaction ->
                        listDetailNav.navigateToDetail(ViewTransaction(transaction.toTransaction()))
                        onIntent(DashboardPageIntent.NavigateToTransaction(transaction))
                    },
                )
            }

            // Detail pane entry - account transactions
            entry<ViewAccountTransactions>(
                metadata = ListDetailSceneStrategy.detailPane(),
            ) { navKey ->
                // Per-entry live subscription for fresh account lookup
                val entryState by viewModel.state.collectAsState()
                val entryAccounts by entryState.accounts.collectAsState(emptyList())
                val account = entryAccounts.find { it.id == navKey.accountId }
                if (account != null) {
                    // Show account-specific transactions using TransactionsPane
                    val accountFilter =
                        uiTransactionFilter {
                            accountIn(account)
                        }
                    TransactionsPane(
                        title = "${account.name} - ${stringResource(MR.strings.transactions)}",
                        filter = accountFilter,
                        onBackClick = { listDetailNav.back() },
                        onTransactionClick = { transaction ->
                            listDetailNav.navigateToDetail(ViewTransaction(transaction.toTransaction()))
                        },
                        onAddClick = {
                            onIntent(DashboardPageIntent.CreateTransactionInAccount(account))
                        },
                        topBarSupportingContent = {},
                    )
                } else {
                    EmptyState(
                        title = stringResource(MR.strings.no_account_selected_title).orEmpty(),
                        message = stringResource(MR.strings.no_account_selected_message).orEmpty(),
                        icon = Icons.Outlined.AccountBalance,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            // Extra pane entry - transaction details
            entry<ViewTransaction>(
                metadata = ListDetailSceneStrategy.extraPane(),
            ) { navKey ->
                // Per-entry live subscription — attachments always fresh
                val entryState by viewModel.state.collectAsState()
                val transaction = navKey.transaction?.toUiTransactionDetails()
                if (transaction != null) {
                    val attachments = entryState.transactionAttachments[transaction.id] ?: emptyList()
                    TransactionDetailView(
                        transaction = transaction,
                        attachments = attachments,
                        onEdit = { onIntent(DashboardPageIntent.UpdateTransaction(transaction)) },
                        onDelete = { onIntent(DashboardPageIntent.DeleteTransaction(transaction)) },
                        onDuplicate = { onIntent(DashboardPageIntent.DuplicateTransaction(transaction)) },
                    )
                } else {
                    EmptyState(
                        title = stringResource(MR.strings.no_transaction_selected_title).orEmpty(),
                        message = stringResource(MR.strings.no_transaction_selected_message).orEmpty(),
                        icon = Icons.Outlined.Receipt,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        // Add/Edit Transaction Dialog
        if (state.showAddEditDialog) {
            AddEditTransactionDialog(
                transaction = state.underProcessTransaction,
                accounts = accounts,
                categories = state.categories,
                attachments = state.selectedAttachments,
                onDismiss = { onIntent(DashboardPageIntent.CancelUpdateTransaction) },
                onSave = {
                    onIntent(DashboardPageIntent.ConfirmUpdateTransaction(it))
                },
                onPickImages = { onIntent(DashboardPageIntent.PickImages) },
                onDeleteAttachment = { attachment ->
                    onIntent(DashboardPageIntent.DeleteAttachment(attachment))
                },
            )
        }

        // Start Over Dialog
        if (state.showStartOverDialog) {
            StartOverDialog(
                accounts = accounts,
                existingSession = state.startOverEditingSession,
                onConfirm = { name, snapshots ->
                    onIntent(DashboardPageIntent.ConfirmStartOver(name, snapshots))
                },
                onDismiss = { onIntent(DashboardPageIntent.DismissStartOverDialog) },
            )
        }

        // Fix Balance Dialog
        if (state.showFixBalanceDialog && state.fixBalanceAccount != null) {
            FixBalanceDialog(
                account = state.fixBalanceAccount!!,
                onApply = { account, actualBalance, isIncrease ->
                    onIntent(DashboardPageIntent.ApplyBalanceAdjustment(account, actualBalance, isIncrease))
                },
                onDismiss = { onIntent(DashboardPageIntent.DismissFixBalanceDialog) },
            )
        }

        // Biometric Suggestion Dialog
        if (state.showBiometricSuggestion) {
            BiometricSuggestionDialog(
                onEnable = { onIntent(DashboardPageIntent.EnableBiometricFromSuggestion) },
                onDismiss = { onIntent(DashboardPageIntent.DismissBiometricSuggestion) },
            )
        }

        // Expandable FAB (bottom-end overlay)
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(AppTheme.dimens.default),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
        ) {
            // Start Over action
            AnimatedVisibility(
                visible = isFabExpanded,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
                ) {
                    Text(
                        text = stringResource(MR.strings.start_over).orEmpty(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    SmallFloatingActionButton(
                        onClick = {
                            isFabExpanded = false
                            onIntent(DashboardPageIntent.ShowStartOverDialog)
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(MR.strings.start_over).orEmpty(),
                        )
                    }
                }
            }

            // Fix Balance action
            AnimatedVisibility(
                visible = isFabExpanded,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
                ) {
                    Text(
                        text = stringResource(MR.strings.fix_balance).orEmpty(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    SmallFloatingActionButton(
                        onClick = {
                            isFabExpanded = false
                            val firstAccount = accounts.firstOrNull()
                            onIntent(DashboardPageIntent.ShowFixBalanceDialog(firstAccount))
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = stringResource(MR.strings.fix_balance).orEmpty(),
                        )
                    }
                }
            }

            // Add Transaction action
            AnimatedVisibility(
                visible = isFabExpanded,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
                ) {
                    Text(
                        text = stringResource(MR.strings.add_transaction).orEmpty(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    SmallFloatingActionButton(
                        onClick = {
                            isFabExpanded = false
                            val firstAccount = accounts.firstOrNull() ?: return@SmallFloatingActionButton
                            onIntent(DashboardPageIntent.CreateTransactionInAccount(firstAccount))
                        },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(MR.strings.add_transaction).orEmpty(),
                        )
                    }
                }
            }

            // Main FAB
            FloatingActionButton(
                onClick = { isFabExpanded = !isFabExpanded },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

/**
 * Dashboard list pane displaying all dashboard sections
 */
@Composable
private fun DashboardListPane(
    state: DashboardPageState,
    onIntent: (DashboardPageIntent) -> Unit,
    onAccountClick: (UiBankAccount) -> Unit,
    onTransactionClick: (com.lightfeather.designsystem.model.UiTransaction) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                modifier = Modifier.offset(x = -AppTheme.dimens.small),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
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
                modifier = Modifier.height(AppTheme.dimens.massive * 4f),
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.compact),
            ) {
                items(accountsList.take(state.displayedAccountsLimit)) { account ->
                    DashboardAccountCard(
                        account = account,
                        onClick = { onAccountClick(account) },
                    )
                }
            }
        }

        // Spending Analytics
        SpendingAnalyticsCard(
            spendingAnalytics = state.spendingAnalytics,
            isLoading = state.isAnalyticsLoading,
        )

        // Recent Activity Section (Timeline with transactions + Start Over markers)
        DashboardSectionHeader(
            title = stringResource(MR.strings.recent_activity).orEmpty(),
            onSeeAllClick = { onIntent(DashboardPageIntent.NavigateToTransactions) },
        )

        if (state.recentTimelineItems.isEmpty()) {
            EmptyState(
                title = stringResource(MR.strings.no_transactions_title).orEmpty(),
                message = stringResource(MR.strings.no_transactions_message).orEmpty(),
                icon = Icons.Outlined.Receipt,
                modifier = Modifier.height(AppTheme.dimens.massive * 3),
            )
        } else {
            TransactionTimelineColumn(
                items = state.recentTimelineItems,
                onTransactionClick = onTransactionClick,
                onMarkerToggle = { sessionId ->
                    onIntent(DashboardPageIntent.ToggleStartOverMarker(sessionId))
                },
                onMarkerEdit = { session ->
                    onIntent(DashboardPageIntent.EditStartOverSession(session))
                },
                onMarkerDelete = { sessionId ->
                    onIntent(DashboardPageIntent.DeleteStartOverSession(sessionId))
                },
                transactionAttachments = state.transactionAttachments,
                onTransactionToggle = { id ->
                    onIntent(DashboardPageIntent.ToggleTransactionExpansion(id))
                },
                onTransactionEdit = { tx ->
                    onIntent(DashboardPageIntent.NavigateToTransaction(tx))
                },
                onTransactionDelete = { tx ->
                    onIntent(DashboardPageIntent.DeleteTransactionById(tx.id))
                },
            )
        }
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

@Preview(showBackground = true)
@Composable
private fun DashboardPagePreview() {
    AppTheme {
        DashboardListPane(
            state = DashboardPageState.dummy,
            onIntent = {},
            onAccountClick = {},
            onTransactionClick = {},
        )
    }
}
