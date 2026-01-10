package com.lightfeather.masarify.template.transactionspane

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    filter: UiTransactionFilter?,
    viewModel: TransactionsPaneViewModel,
    onBackClick: () -> Unit,
    onTransactionClick: (UiTransaction) -> Unit,
    topBarSupportingContent: @Composable () -> Unit,
) {
    val transactions by viewModel.transactions.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()

    val isEmpty = transactions.data.isEmpty()
    val isLoading = transactions.data.isEmpty() && transactions.page == 0
    val isFiltered = filter != null && !filter.isEmpty()

    Column {

        // Supporting content (title, filter button) provided by parent
        topBarSupportingContent()

        when {

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
                    items(transactions.data) {
                        TransactionItem(
                            transaction = it,
                            onClick = { onTransactionClick(it) },
                        )
                    }
                }
            }
        }

        // Compact pagination controls (only shown when there are multiple pages)
        if (transactions.totalPages > 1) {
            CompactPaginationControls(
                currentPage = currentPage,
                totalPages = transactions.totalPages,
                onPageChange = viewModel::setPage,
                onPreviousPage = viewModel::previousPage,
                onNextPage = viewModel::nextPage,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
