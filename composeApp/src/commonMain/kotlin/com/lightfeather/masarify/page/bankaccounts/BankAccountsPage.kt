package com.lightfeather.masarify.page.bankaccounts

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.navigation3.runtime.NavKey
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.molecules.button.FloatingActionButton
import com.lightfeather.designsystem.component.organisms.AccountsHeader
import com.lightfeather.designsystem.component.organisms.TransactionDetailView
import com.lightfeather.designsystem.component.organisms.dialog.AddEditTransactionDialog
import com.lightfeather.designsystem.component.organisms.listitem.BankAccountItem
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.uiTransactionFilter
import com.lightfeather.designsystem.modifier.applyIf
import com.lightfeather.designsystem.shape.inWardTriangleCutShape
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.masarify.asSlug
import com.lightfeather.masarify.getPlatform
import com.lightfeather.masarify.mappers.toTransaction
import com.lightfeather.masarify.mappers.toUiTransaction
import com.lightfeather.masarify.navigation.Display
import com.lightfeather.masarify.navigation.LocalNavigator
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.PreviewNavigator
import com.lightfeather.masarify.page.createbankaccount.CreateBankAccountPage
import com.lightfeather.masarify.page.transactions.ViewTransaction
import com.lightfeather.masarify.template.transactionspane.TransactionsPane
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BankAccountsPage(
    viewModel: BankAccountsPageViewModel = koinViewModel(),
    navigator: Navigator = LocalNavigator.current,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onIntent(BankAccountsPageIntent.LoadData)
    }

    BankAccountsPageContent(
        state = state,
        onIntent = viewModel::onIntent,
        navigator = navigator,
    )
}

// Composable UI function with navigation and detail pane management - length is acceptable for UI composition
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun BankAccountsPageContent(
    state: BankAccountsPageState,
    onIntent: (BankAccountsPageIntent) -> Unit,
    navigator: Navigator,
) {
    // Create scoped list-detail navigator
    val listDetailNav = navigator.forListDetail(BankAccountsList)

    // Create list-detail scene strategy for adaptive layout
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
        directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo(true)),
        adaptStrategies = ListDetailPaneScaffoldDefaults.adaptStrategies(
            detailPaneAdaptStrategy = AdaptStrategy.Reflow(ThreePaneScaffoldRole.Primary),
            listPaneAdaptStrategy = AdaptStrategy.Reflow(ThreePaneScaffoldRole.Secondary),
            extraPaneAdaptStrategy = AdaptStrategy.Reflow(ThreePaneScaffoldRole.Tertiary)
        )
    )

    // Collect state flows
    val accounts by state.bankAccounts.collectAsState(emptyList())
    val userAccountsCurrencies by state.userAccountsCurrencies.collectAsState(emptyList())
    val defaultCurrency by state.defaultCurrency.collectAsState(null)

    // Helper to find account by ID
    val findAccount: (String) -> UiBankAccount? = { accountId ->
        accounts.find { it.id == accountId }
    }

    listDetailNav.Display(
        sceneStrategy = listDetailStrategy,
        modifier = Modifier,
    ) {
        // List pane entry
        entry<BankAccountsList>(
            metadata =
                ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        EmptyState(
                            title = stringResource(MR.strings.no_account_selected_title),
                            message = stringResource(MR.strings.no_account_selected_message),
                            icon = Icons.Outlined.AccountBalance,
                            modifier = Modifier.fillMaxSize(),
                        )
                    },
                ),
        ) {
            AccountsListPane(
                accounts = accounts,
                userAccountsCurrencies = userAccountsCurrencies,
                totalAmountInSelectedOrDefaultCurrency = state.totalAmountInSelectedOrDefaultCurrency,
                defaultCurrency = defaultCurrency,
                selectedCurrency = state.selectedCurrency,
                selectedAccount = state.selectedAccount,
                onAddAccount = {
                    listDetailNav.navigateToDetail(AddBankAccount)
                },
                onAccountClick = {
                    listDetailNav.navigateToDetail(ViewBankAccount(it.id))
                    onIntent(BankAccountsPageIntent.SelectAccount(it))
                },
                onUpdateAccount = {
                    listDetailNav.navigateToDetail(UpdateBankAccount(it.id))
                    onIntent(BankAccountsPageIntent.SelectAccount(it))
                },
                onDeleteAccount = { onIntent(BankAccountsPageIntent.DeleteBankAccount(it)) },
                onCreateTransactionFromAccount = {
                    onIntent(BankAccountsPageIntent.CreateTransactionInAccount(it))
                },
                onTransferFromAccount = {
                    onIntent(BankAccountsPageIntent.TransferFromAccount(it))
                },
                onCurrencyClick = { onIntent(BankAccountsPageIntent.SelectCurrency(it)) },
            )
        }

        // Add account detail pane
        entry<AddBankAccount>(
            metadata = ListDetailSceneStrategy.detailPane(),
        ) {
            CreateBankAccountPage(
                account = UiBankAccount.empty,
                onBack = { listDetailNav.back() },
            )
        }

        // View account detail pane
        entry<ViewBankAccount>(
            metadata = ListDetailSceneStrategy.detailPane(),
        ) { navKey ->

            val account = findAccount(navKey.accountId)

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
                    topBarSupportingContent = {},
                )
            } else {
                EmptyState(
                    title = stringResource(MR.strings.no_account_selected_title),
                    message = stringResource(MR.strings.no_account_selected_message),
                    icon = Icons.Outlined.AccountBalance,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        entry<ViewTransaction>(
            metadata = ListDetailSceneStrategy.extraPane(),
        ) { navKey ->
            val transaction = navKey.transaction?.toUiTransaction()
            if (transaction != null) {
                TransactionDetailView(
                    transaction = transaction,
                    onEdit = { onIntent(BankAccountsPageIntent.UpdateTransaction(transaction)) },
                    onDelete = { onIntent(BankAccountsPageIntent.DeleteTransaction(transaction)) },
                    onDuplicate = { onIntent(BankAccountsPageIntent.DuplicateTransaction(transaction)) },
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
        // Update account detail pane
        entry<UpdateBankAccount>(
            metadata = ListDetailSceneStrategy.detailPane(),
        ) { navKey ->
            val account = findAccount(navKey.accountId)
            if (account != null) {
                CreateBankAccountPage(
                    account = account,
                    onBack = { listDetailNav.back() },
                )
            } else {
                EmptyState(
                    title = stringResource(MR.strings.no_account_selected_title),
                    message = stringResource(MR.strings.no_account_selected_message),
                    icon = Icons.Outlined.AccountBalance,
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
            onDismiss = { onIntent(BankAccountsPageIntent.CancelUpdateTransaction) },
            onSave = { onIntent(BankAccountsPageIntent.CancelUpdateTransaction) },
        )
    }
}

// Composable UI function with complex layout - length is acceptable for UI composition
@Suppress("LongMethod")
@Composable
private fun AccountsListPane(
    accounts: List<UiBankAccount>,
    userAccountsCurrencies: List<UiCurrency>,
    totalAmountInSelectedOrDefaultCurrency: String,
    defaultCurrency: UiCurrency?,
    selectedCurrency: UiCurrency?,
    selectedAccount: UiBankAccount?,
    onAddAccount: () -> Unit,
    onAccountClick: (UiBankAccount) -> Unit,
    onUpdateAccount: (UiBankAccount) -> Unit,
    onDeleteAccount: (UiBankAccount) -> Unit,
    onCreateTransactionFromAccount: (UiBankAccount) -> Unit,
    onTransferFromAccount: (UiBankAccount) -> Unit,
    onCurrencyClick: (UiCurrency?) -> Unit,
) {
    val isMobile = getPlatform().asSlug()?.isMobile() == true
    val primaryColor = MaterialTheme.colorScheme.primary
    val lazyListState = rememberLazyListState()

    // Calculate selected account position for line drawing
    val selectedAccountIndex =
        remember(accounts, selectedAccount) {
            if (selectedAccount != null) {
                accounts.indexOfFirst { it.id == selectedAccount.id }
            } else {
                -1
            }
        }

    Box(
        modifier =
            Modifier.applyIf(
                isMobile.not(),
                Modifier.fillMaxHeight().drawWithContent {
                    // Draw the content first
                    drawContent()

                    // Then draw the line on top
                    val strokeWidth = AppTheme.dimens.hairline.value * density
                    val x = size.width - strokeWidth / 2

                    // If no account is selected, draw full line
                    if (selectedAccount == null || selectedAccountIndex == -1) {
                        drawLine(
                            primaryColor,
                            Offset(x, 0f),
                            Offset(x, size.height),
                            strokeWidth,
                        )
                    } else {
                        // Calculate approximate position of selected account
                        // Account for header items (AccountsHeader + section title = 2 items)
                        val accountItemIndex = selectedAccountIndex + 2
                        val visibleItems = lazyListState.layoutInfo.visibleItemsInfo

                        // Find the selected account item in visible items
                        val selectedItem = visibleItems.find { it.index == accountItemIndex }

                        if (selectedItem != null) {
                            // Convert item coordinates to canvas coordinates
                            val itemTop = selectedItem.offset.toFloat()
                            val itemBottom = (selectedItem.offset + selectedItem.size).toFloat()

                            // Draw first line: from top to start of selected account
                            if (itemTop > 0) {
                                drawLine(
                                    primaryColor,
                                    Offset(x, 0f),
                                    Offset(x, itemTop + AppTheme.dimens.hairline.toPx()),
                                    strokeWidth,
                                )
                            }

                            // Draw second line: from end of selected account to bottom
                            if (itemBottom < size.height) {
                                drawLine(
                                    primaryColor,
                                    Offset(x, itemBottom - AppTheme.dimens.hairline.toPx()),
                                    Offset(x, size.height),
                                    strokeWidth,
                                )
                            }
                        } else {
                            // If selected item is not visible, draw full line
                            drawLine(
                                primaryColor,
                                Offset(x, 0f),
                                Offset(x, size.height),
                                strokeWidth,
                            )
                        }
                    }
                },
            ),
    ) {
        Box {
            if (accounts.isEmpty()) {
                EmptyState(
                    title = stringResource(MR.strings.no_accounts_title),
                    message = stringResource(MR.strings.no_accounts_message),
                    icon = Icons.Outlined.AccountBalance,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = AppTheme.dimens.large),
                ) {
                    // Wealth Summary Header
                    item {
                        AccountsHeader(
                            totalAmountInSelectedOrDefaultCurrency = totalAmountInSelectedOrDefaultCurrency,
                            userAccountsCurrencies = userAccountsCurrencies,
                            onCurrencyClick = onCurrencyClick,
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
                        // Animate the selection state
                        val isSelected = account == selectedAccount
                        val animatedCutSize by animateDpAsState(
                            targetValue = if (isSelected) AppTheme.dimens.default else AppTheme.dimens.none,
                            animationSpec = tween(durationMillis = 300),
                            label = "cutSize",
                        )
                        val animatedTrianglePosition by animateFloatAsState(
                            targetValue = if (isSelected) 0.4f else 0.5f,
                            animationSpec = tween(durationMillis = 300),
                            label = "trianglePosition",
                        )
                        val animatedStrokeWidth by animateDpAsState(
                            targetValue = if (isSelected) AppTheme.dimens.small else AppTheme.dimens.none,
                            animationSpec = tween(durationMillis = 300),
                            label = "strokeWidth",
                        )

                        // Create animated shape
                        val animatedCardShape =
                            inWardTriangleCutShape(
                                tailSize = animatedCutSize,
                                topSpacePercentage = animatedTrianglePosition,
                            )

                        BankAccountItem(
                            account,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clip(animatedCardShape)
                                    .drawWithContent {
                                        // Draw the card content first
                                        drawContent()

                                        // Only draw border if selected (stroke width > 0)
                                        if (animatedStrokeWidth.value > 0f) {
                                            // Draw the complete border with triangular connector
                                            val strokeWidth = animatedStrokeWidth.toPx()
                                            val cut = with(density) { animatedCutSize.toPx() }
                                            val triangleTopY = size.height * animatedTrianglePosition
                                            val triangleCenterY = triangleTopY + cut / 2f
                                            val triangleBottomY = triangleTopY + cut

                                            // Create a path that follows the exact same shape as the clip
                                            val borderPath =
                                                Path().apply {
                                                    // Start from top-right
                                                    moveTo(size.width, 0f)
                                                    // Draw down to the start of the inward cut
                                                    lineTo(size.width, triangleTopY)
                                                    // Draw the inward triangular cut (pointing inward to the left)
                                                    lineTo(size.width - cut, triangleCenterY)
                                                    // Complete the triangle by going back to the right edge
                                                    lineTo(size.width, triangleBottomY)
                                                    // Draw down to bottom-right corner
                                                    lineTo(size.width, size.height)
                                                }

                                            // Draw the border path
                                            drawPath(
                                                path = borderPath,
                                                color = primaryColor,
                                                style = Stroke(width = strokeWidth),
                                            )
                                        }
                                    },
                            shape = RectangleShape,
                            onClick = { onAccountClick(account) },
                            onTransfer = { onTransferFromAccount(account) },
                            onEdit = { onUpdateAccount(account) },
                            onDelete = { onDeleteAccount(account) },
                            onCreateTransaction = { onCreateTransactionFromAccount(account) },
                        )
                    }
                }
            }

            // FloatingActionButton - positioned last to be on top
            FloatingActionButton(
                onClick = onAddAccount,
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(MR.strings.add_account),
                modifier = Modifier.align(Alignment.BottomEnd).padding(AppTheme.dimens.default),
            )
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
            navigator = PreviewNavigator,
        )
    }
}
