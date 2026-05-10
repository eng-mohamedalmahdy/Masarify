package tech.lightfeather.masarify.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.AdvancedPaginationControls
import tech.lightfeather.designsystem.component.molecules.EmptyState
import tech.lightfeather.designsystem.component.molecules.button.FloatingActionButton
import tech.lightfeather.designsystem.component.organisms.listitem.TransactionItem
import tech.lightfeather.designsystem.model.UiTransaction
import tech.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource

/**
 * Reusable transactions list component with pagination
 * Shows a list of transactions with filter indicator, pagination controls, and FAB
 *
 * @param transactions List of transactions to display
 * @param currentPage Current page number (0-indexed)
 * @param totalPages Total number of pages
 * @param activeFilterCount Number of active filters
 * @param onAddTransaction Callback when add transaction FAB is clicked
 * @param onTransactionClick Callback when a transaction is clicked
 * @param onFilterClick Callback when filter button is clicked
 * @param onPageChange Callback when page changes (receives 0-indexed page)
 * @param onPreviousPage Callback for previous page button
 * @param onNextPage Callback for next page button
 * @param modifier Modifier for the component
 * @param title Optional title override (defaults to "Transactions")
 * @param showPagination Whether to show pagination controls (default: true when totalPages > 1)
 */
@Composable
fun TransactionsList(
    transactions: List<UiTransaction>,
    currentPage: Int,
    totalPages: Int,
    activeFilterCount: Int,
    onAddTransaction: () -> Unit,
    onTransactionClick: (UiTransaction) -> Unit,
    onFilterClick: () -> Unit,
    onPageChange: (Int) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(MR.strings.transactions_title),
    showPagination: Boolean = totalPages > 1,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimens.spacing.padding.medium),
            ) {
                // Title row with filter button
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        // Filter button with reactive badge
                        // Key ensures recomposition when filter count changes
                        androidx.compose.runtime.key(activeFilterCount) {
                            IconButton(onClick = onFilterClick) {
                                BadgedBox(
                                    badge = {
                                        // Always provide badge lambda, conditionally render content
                                        if (activeFilterCount > 0) {
                                            Badge {
                                                Text("$activeFilterCount")
                                            }
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector =
                                            if (activeFilterCount > 0) {
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
            }

            // Transactions List
            if (transactions.isEmpty()) {
                EmptyState(
                    title =
                        if (activeFilterCount > 0) {
                            stringResource(MR.strings.no_filtered_transactions_title)
                        } else {
                            stringResource(MR.strings.no_transactions_title)
                        },
                    message =
                        if (activeFilterCount > 0) {
                            stringResource(MR.strings.no_filtered_transactions_message)
                        } else {
                            stringResource(MR.strings.no_transactions_message)
                        },
                    icon = Icons.Outlined.Receipt,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                ) {
                    items(
                        items = transactions,
                        key = { it.id },
                    ) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { onTransactionClick(transaction) },
                        )
                    }
                }

                // Pagination controls (1-indexed for display)
                if (showPagination) {
                    AdvancedPaginationControls(
                        currentPage = currentPage + 1, // Convert to 1-indexed
                        totalPages = totalPages,
                        onPageChange = { page -> onPageChange(page - 1) }, // Convert back to 0-indexed
                        onPreviousPage = onPreviousPage,
                        onNextPage = onNextPage,
                    )
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onAddTransaction,
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(MR.strings.add_transaction),
            modifier = Modifier.align(Alignment.BottomEnd).padding(AppTheme.dimens.default),
        )
    }
}
