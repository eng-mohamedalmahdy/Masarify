package com.lightfeather.masarify.template.transactionspane

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.organisms.listitem.TransactionItem
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TransactionsPane(
    filter: UiTransactionFilter,
    viewModel: TransactionsPanePageViewModel,
) {
    // Update the filter when it changes
    LaunchedEffect(filter) {
        viewModel.updateFilter(filter)
    }

    val transactions = viewModel.transactions.collectAsLazyPagingItems()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isEmpty by viewModel.isEmpty.collectAsState()
    val isFiltered by viewModel.isFiltered.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()

    // Update empty state based on PagingData load state
    LaunchedEffect(transactions.loadState.refresh, transactions.itemCount) {
        Napier.d(
            "Transactions loaded successfully ${transactions.itemCount} load Status = ${transactions.loadState.refresh}",
            tag = "TransactionsPane"
        )

        when {
            transactions.loadState.refresh is LoadState.NotLoading -> {
                viewModel.updateEmptyState(transactions.itemCount == 0)
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize(),
    ) {
        if (isEmpty && transactions.loadState.refresh is LoadState.NotLoading) {
            // Show empty state
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
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.hairline),
            ) {
                items(
                    count = transactions.itemCount,
                    key = transactions.itemKey { it.id },
                ) { index ->
                    val transaction = transactions[index]
                    transaction?.let {
                        TransactionItem(
                            transaction = it,
                            onClick = { viewModel.onTransactionClick(it.id) },
                        )
                    }
                }

                when (transactions.loadState.append) {
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
