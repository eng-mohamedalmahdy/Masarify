package com.lightfeather.masarify.page.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation3.runtime.NavKey
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.organisms.AccountsHeader
import com.lightfeather.designsystem.component.organisms.AddEditTransactionPageContent
import com.lightfeather.designsystem.component.organisms.TransactionDetailView
import com.lightfeather.designsystem.component.organisms.dialog.AdvancedFilterDialog
import com.lightfeather.designsystem.model.PageSize
import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.designsystem.model.UiTransactionDetails
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.masarify.mappers.toTransaction
import com.lightfeather.masarify.mappers.toUiTransactionDetails
import com.lightfeather.masarify.navigation.Display
import com.lightfeather.masarify.navigation.LocalNavigator
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.PreviewNavigator
import com.lightfeather.masarify.template.transactionspane.TransactionsPane
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransactionsPage(
    openAddDialog: Boolean = false,
    transactionType: UiTransactionType? = null,
    fromAccountId: String? = null,
    categoryId: String? = null,
    viewModel: TransactionsPageViewModel = koinViewModel(),
    navigator: Navigator = LocalNavigator.current,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onIntent(TransactionsPageIntent.LoadData)
    }

    TransactionsPageContent(
        state = state,
        onIntent = viewModel::onIntent,
        navigator = navigator,
        openAddTransaction = openAddDialog,
        initialTransactionType = transactionType,
        fromAccountId = fromAccountId,
        categoryId = categoryId,
        onPickImages = viewModel::pickImagesAndReturn,
    )
}

@Suppress("LongMethod", "LongParameterList")
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun TransactionsPageContent(
    state: TransactionsPageState,
    onIntent: (TransactionsPageIntent) -> Unit,
    navigator: Navigator,
    openAddTransaction: Boolean = false,
    initialTransactionType: UiTransactionType? = null,
    fromAccountId: String? = null,
    categoryId: String? = null,
    onPickImages: suspend () -> List<UiAttachment> = { emptyList() },
) {
    // Create scoped list-detail navigator
    val listDetailNav = navigator.forListDetail(TransactionsList)

    // Create list-detail scene strategy for adaptive layout
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    // Collect state flows
    val accounts by state.accounts.collectAsState(emptyList())
    val categories by state.categories.collectAsState(emptyList())
    val currencies by state.currencies.collectAsState(emptyList())
    val userAccountsCurrencies by state.userAccountsCurrencies.collectAsState(emptyList())
    val defaultCurrency by state.defaultCurrency.collectAsState(null)

    // Handle deep-link: open add transaction pane with optional context
    LaunchedEffect(openAddTransaction, initialTransactionType, fromAccountId, categoryId) {
        if (openAddTransaction) {
            onIntent(
                TransactionsPageIntent.ShowAddDialogWithType(
                    type = initialTransactionType ?: UiTransactionType.EXPENSE,
                    fromAccountId = fromAccountId,
                    categoryId = categoryId,
                ),
            )
            listDetailNav.navigateToDetail(AddTransaction)
        }
    }

    // Key the whole Display on changing inputs that the navigation entry otherwise caches out.
    // This forces recomposition so the AccountsHeader updates and chips become responsive.
    key(state.filter, state.selectedCurrency, state.totalAmountInSelectedOrDefaultCurrency) {
        listDetailNav.Display(
            sceneStrategy = listDetailStrategy,
            modifier = Modifier,
        ) {
            // List pane entry
            entry<TransactionsList>(
                metadata =
                    ListDetailSceneStrategy.listPane(
                        detailPlaceholder = {
                            EmptyState(
                                title = stringResource(MR.strings.no_transaction_selected_title),
                                message = stringResource(MR.strings.no_transaction_selected_message),
                                icon = Icons.Outlined.Receipt,
                                modifier = Modifier.fillMaxSize(),
                            )
                        },
                    ),
            ) {
                // Key on filter to force recomposition when filter changes
                // Navigation entry blocks cache content and don't track external state changes

                TransactionsPane(
                    title = stringResource(MR.strings.transactions_title),
                    filter = state.filter,
                    onBackClick = { listDetailNav.back() },
                    onTransactionClick = { transaction ->
                        listDetailNav.navigateToDetail(ViewTransaction(transaction.toTransaction()))
                    },
                    onAddClick = {
                        listDetailNav.navigateToDetail(AddTransaction)
                    },
                    topBarSupportingContent = {
                        Column {
                            // Accounts Header - Wealth Summary
                            AccountsHeader(
                                totalAmountInSelectedOrDefaultCurrency = state.totalAmountInSelectedOrDefaultCurrency,
                                userAccountsCurrencies = userAccountsCurrencies,
                                onCurrencyClick = { onIntent(TransactionsPageIntent.SelectCurrency(it)) },
                                defaultCurrency = defaultCurrency,
                                selectedCurrency = state.selectedCurrency,
                                totalAccounts = accounts.size,
                                shape = RectangleShape,
                            )

                            Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(MR.strings.transactions_title),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )

                                // Filter button with reactive badge
                                // Key ensures recomposition when filter count changes
                                key(state.filter.getActiveFilterCount()) {
                                    IconButton(
                                        onClick = { onIntent(TransactionsPageIntent.ShowFilterDialog) },
                                    ) {
                                        BadgedBox(
                                            badge = {
                                                // Always provide badge lambda, conditionally render content
                                                if (state.filter.getActiveFilterCount() > 0) {
                                                    Badge { Text("${state.filter.getActiveFilterCount()}") }
                                                }
                                            },
                                        ) {
                                            Icon(
                                                imageVector =
                                                    if (state.filter.getActiveFilterCount() > 0) {
                                                        Icons.Default.FilterListOff
                                                    } else {
                                                        Icons.Default.FilterList
                                                    },
                                                contentDescription = stringResource(MR.strings.filter_transactions),
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))

                            Text(
                                text = stringResource(MR.strings.transactions_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                )
            }

            // View transaction detail pane
            entry<ViewTransaction>(
                metadata = ListDetailSceneStrategy.detailPane(),
            ) { navKey ->
                val transaction = navKey.transaction?.toUiTransactionDetails()
                if (transaction != null) {
                    val attachments = state.transactionAttachments[transaction.id] ?: emptyList()
                    TransactionDetailPane(
                        transaction = transaction,
                        attachments = attachments,
                        onEdit = {
                            onIntent(TransactionsPageIntent.PrepareEditTransaction(transaction))
                            listDetailNav.navigateToDetail(EditTransaction(navKey.transaction))
                        },
                        onDelete = {
                            onIntent(TransactionsPageIntent.DeleteTransaction(transaction))
                            listDetailNav.back()
                        },
                        onDuplicate = {
                            onIntent(TransactionsPageIntent.DuplicateTransaction(transaction))
                            listDetailNav.navigateToDetail(AddTransaction)
                        },
                    )
                } else {
                    EmptyState(
                        title = stringResource(MR.strings.no_transaction_selected_title),
                        message = stringResource(MR.strings.no_transaction_selected_message),
                        icon = Icons.Outlined.Receipt,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            // Add transaction pane
            entry<AddTransaction>(
                metadata = ListDetailSceneStrategy.detailPane(),
            ) {
                AddEditTransactionPageContent(
                    transaction = null,
                    lockedFromAccount = state.lockedFromAccount,
                    initialAccount = state.defaultAccount,
                    initialCategory = state.initialCategory,
                    accounts = accounts,
                    categories = categories,
                    initialAttachments = emptyList(),
                    onBack = {
                        onIntent(TransactionsPageIntent.ClearTransactionContext)
                        listDetailNav.back()
                    },
                    onSave = { data -> onIntent(TransactionsPageIntent.CreateTransaction(data)) },
                    onPickImages = onPickImages,
                )
            }

            // Edit transaction pane
            entry<EditTransaction>(
                metadata = ListDetailSceneStrategy.detailPane(),
            ) { navKey ->
                val editTransaction =
                    navKey.transaction?.toUiTransactionDetails()
                        ?: state.editingTransaction
                AddEditTransactionPageContent(
                    transaction = editTransaction,
                    lockedFromAccount = state.lockedFromAccount,
                    initialAccount = state.defaultAccount,
                    initialCategory = state.initialCategory,
                    accounts = accounts,
                    categories = categories,
                    initialAttachments = state.selectedAttachments,
                    onBack = {
                        onIntent(TransactionsPageIntent.ClearTransactionContext)
                        listDetailNav.back()
                    },
                    onSave = { data -> onIntent(TransactionsPageIntent.UpdateTransaction(data)) },
                    onPickImages = onPickImages,
                )
            }
        }

        // Filter Dialog
        if (state.showFilterDialog) {
            AdvancedFilterDialog(
                filter = state.filter,
                accounts = accounts,
                categories = categories,
                currencies = currencies,
                onDismiss = { onIntent(TransactionsPageIntent.HideFilterDialog) },
                onApply = { filter ->
                    onIntent(TransactionsPageIntent.UpdateFilter(filter))
                },
                onSave = { name, filter ->
                    onIntent(TransactionsPageIntent.SaveFilter(name, filter))
                },
            )
        }
    }
}

@Composable
private fun TransactionDetailPane(
    transaction: UiTransactionDetails,
    attachments: List<UiAttachment>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
) {
    TransactionDetailView(
        transaction = transaction,
        attachments = attachments,
        onEdit = onEdit,
        onDelete = onDelete,
        onDuplicate = onDuplicate,
    )
}

@Preview
@Composable
fun TransactionsPagePreview() {
    TransactionsPageContent(
        state =
            TransactionsPageState(
                currentPage = 0,
                pageSize = PageSize.MEDIUM,
                totalCount = 3,
            ),
        onIntent = {},
        navigator = PreviewNavigator,
    )
}
