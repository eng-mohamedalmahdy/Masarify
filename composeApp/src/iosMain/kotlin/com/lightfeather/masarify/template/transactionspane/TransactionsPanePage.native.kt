package com.lightfeather.masarify.template.transactionspane

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.map
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.CompactPaginationControls
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.organisms.listitem.TransactionItem
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.masarify.mappers.toTransactionFilter
import com.lightfeather.masarify.mappers.toUiTransaction
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.map

/**
 * iOS implementation with Paging3 support
 * Complexity is acceptable for platform-specific presentation logic
 */
@Suppress("CyclomaticComplexMethod")
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
    // Cast paging data source for iOS
    val pagingData = pagingDataSource as? TransactionsPagingData

    // Create pager with filter reactivity
    val pager =
        remember(filter) {
            pagingData?.let { data ->
                Pager(
                    config =
                        PagingConfig(
                            pageSize = 20,
                            prefetchDistance = 5,
                            initialLoadSize = 40,
                            enablePlaceholders = false,
                        ),
                    pagingSourceFactory = {
                        val db =
                            data.sharedDatabase as
                                com.lightfeather.data.local.database.drivers.SharedDatabase
                        TransactionsPagingSource(
                            sharedDatabase = db,
                            getFilteredTransactionsPaged = data.getFilteredTransactionsPaged,
                            filter = filter.toTransactionFilter(),
                        )
                    },
                )
            }
        }

    val pagingItems =
        remember(pager) {
            pager?.flow?.map { pagingData ->
                pagingData.map { transaction -> transaction.toUiTransaction() }
            }
        }?.collectAsLazyPagingItems()

    // Track empty state based on paging load state
    LaunchedEffect(pagingItems?.loadState?.refresh, pagingItems?.itemCount) {
        // Empty state is managed by parent
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Supporting content (title, filter button) provided by parent
        topBarSupportingContent()

        when {
            // Show loading on initial load
            pagingItems?.loadState?.refresh is LoadState.Loading && pagingItems.itemCount == 0 -> {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            // Show empty state
            isEmpty &&
                pagingItems?.loadState?.refresh is LoadState.NotLoading &&
                pagingItems.itemCount == 0 -> {
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
                    modifier = Modifier.weight(1f).fillMaxSize(),
                )
            }

            // Show transactions with infinite scroll
            else -> {
                if (pagingItems != null) {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.hairline),
                    ) {
                        items(
                            count = pagingItems.itemCount,
                            key = pagingItems.itemKey { it.id },
                        ) { index ->
                            val transaction = pagingItems[index]
                            transaction?.let {
                                TransactionItem(
                                    transaction = it,
                                    onClick = { onTransactionClick(it.id) },
                                )
                            }
                        }

                        // Loading indicator at bottom when loading more
                        when (pagingItems.loadState.append) {
                            is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(AppTheme.dimens.default),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }

        // Compact pagination control showing approximate position
        // Shows when we have data (for user awareness of position)
        if (pagingItems != null && pagingItems.itemCount > 0 && totalPages > 1) {
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
