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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.lightfeather.designsystem.component.organisms.listitem.TransactionItem
import com.lightfeather.designsystem.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TransactionsPane(viewModel: TransactionsPanePageViewModel) {
    val transactions = viewModel.transactions.collectAsLazyPagingItems()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.hairline)
        ) {
            items(
                count = transactions.itemCount,
                key = transactions.itemKey { it.id }
            ) { index ->
                val transaction = transactions[index]
                transaction?.let {
                    TransactionItem(
                        transaction = it,
                        onClick = { viewModel.onTransactionClick(it.id) }
                    )
                }
            }

            when (transactions.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppTheme.dimens.default),
                            contentAlignment = Alignment.Center
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
