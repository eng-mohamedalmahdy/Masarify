package com.lightfeather.masarify.template.transactionspane

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.molecules.button.FloatingActionButton
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
    onAddClick: () -> Unit,
    onBackClick: () -> Unit,
    onTransactionClick: (UiTransaction) -> Unit,
    topBarSupportingContent: @Composable (() -> Unit),
) {
    // Update filter when it changes
    androidx.compose.runtime.LaunchedEffect(filter) {
        val domainFilter =
            filter?.let {
                com.lightfeather.masarify.mappers
                    .toTransactionFilter(it)
            }
                ?: com.lightfeather.domain.model.transaction.TransactionFilter.EMPTY
        viewModel.updateFilter(domainFilter)
    }

    val transactions = viewModel.transactions.collectAsLazyPagingItems()

    val isEmpty = transactions.itemCount == 0
    val isFiltered = filter != null && !filter.isEmpty()

    Box {
        Column {
            // Supporting content (title, filter button) provided by parent
            topBarSupportingContent()

            when {
                isEmpty -> {
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
                    )
                }

                else -> {
                    Box {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.hairline),
                        ) {
                            items(transactions.itemCount) {
                                TransactionItem(
                                    transaction = transactions[it]!!,
                                    onClick = { onTransactionClick(transactions[it]!!) },
                                )
                            }
                        }
                    }
                }
            }
        }
        // FloatingActionButton - positioned last to be on top
        FloatingActionButton(
            onClick = onAddClick,
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(MR.strings.add_account),
            modifier = Modifier.align(Alignment.BottomEnd).padding(AppTheme.dimens.default),
        )
    }
}
