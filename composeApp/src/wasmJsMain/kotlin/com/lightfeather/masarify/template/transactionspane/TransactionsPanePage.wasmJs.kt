package com.lightfeather.masarify.template.transactionspane

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.CompactPaginationControls
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.organisms.listitem.TransactionItem
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource

@Composable
actual fun TransactionsPane(
    title: String,
    filter: UiTransactionFilter,
    transactions: List<UiTransaction>,
    currentPage: Int,
    totalPages: Int,
    isLoading: Boolean,
    isEmpty: Boolean,
    isFiltered: Boolean,
    onBackClick: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onPageChange: (Int) -> Unit,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit,
    pagingDataSource: Any?,
    topBarSupportingContent: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Supporting content (title, filter button) provided by parent
        topBarSupportingContent()

        when {
            isLoading && transactions.isEmpty() -> {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            isEmpty && !isLoading -> {
                EmptyState(
                    title =
                        stringResource(
                            if (isFiltered) {
                                MR.strings.empty_filtered_transactions_title
                            } else {
                                MR.strings.empty_transactions_title
                            },
                        ),
                    message =
                        stringResource(
                            if (isFiltered) {
                                MR.strings.empty_filtered_transactions_message
                            } else {
                                MR.strings.empty_transactions_message
                            },
                        ),
                    modifier = Modifier.weight(1f),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.hairline),
                ) {
                    items(
                        count = transactions.size,
                        key = { index -> transactions[index].id },
                    ) { index ->
                        TransactionItem(
                            transaction = transactions[index],
                            onClick = { onTransactionClick(transactions[index].id) },
                        )
                    }
                }
            }
        }

        // Compact pagination controls (only shown when there are multiple pages)
        if (totalPages > 1) {
            CompactPaginationControls(
                currentPage = currentPage,
                totalPages = totalPages,
                onPreviousPage = onPreviousPage,
                onNextPage = onNextPage,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
