package com.lightfeather.masarify.template.transactionspane

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.molecules.button.FloatingActionButton
import com.lightfeather.designsystem.component.organisms.TransactionTimeline
import com.lightfeather.designsystem.model.TransactionListItem
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.usecase.GetAllFinancialSessions
import com.lightfeather.masarify.mappers.toUiFinancialSession
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject

@Suppress("LongMethod", "CyclomaticComplexMethod")
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
    val getAllFinancialSessions = koinInject<GetAllFinancialSessions>()

    // Update filter when it changes
    LaunchedEffect(filter) {
        val domainFilter =
            filter?.let {
                com.lightfeather.masarify.mappers
                    .toTransactionFilter(it)
            } ?: com.lightfeather.domain.model.transaction.TransactionFilter.EMPTY
        viewModel.updateFilter(domainFilter)
    }

    val transactions = viewModel.transactions.collectAsLazyPagingItems()

    val isEmpty = transactions.itemCount == 0
    val isFiltered = filter != null && !filter.isEmpty()

    var timelineItems by remember { mutableStateOf<List<TransactionListItem>>(emptyList()) }
    var expandedMarkers by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(transactions.itemSnapshotList) {
        val sessions =
            when (val result = getAllFinancialSessions()) {
                is DomainResult.Success ->
                    result.data.let { flow ->
                        val collected = mutableListOf<com.lightfeather.domain.model.FinancialSession>()
                        flow.collect { collected.addAll(it) }
                        collected
                    }
                is DomainResult.Failure -> emptyList()
            }

        val transactionItems = transactions.itemSnapshotList.items.map { TransactionListItem.TransactionEntry(it) }
        val markerItems =
            sessions.map { session ->
                TransactionListItem.StartOverMarker(
                    session = session.toUiFinancialSession(),
                    isExpanded = session.id.toString() in expandedMarkers,
                )
            }

        val merged =
            (transactionItems + markerItems).sortedByDescending { item ->
                when (item) {
                    is TransactionListItem.TransactionEntry ->
                        item.transaction.dateTime.let { dt ->
                            dt.year.toLong() * 1_0000_000_000L +
                                dt.monthNumber.toLong() * 1_000_000_00L +
                                dt.dayOfMonth.toLong() * 1_000_000L +
                                dt.hour.toLong() * 10_000L +
                                dt.minute.toLong() * 100L +
                                dt.second.toLong()
                        }
                    is TransactionListItem.StartOverMarker ->
                        item.session.timestamp.let { dt ->
                            dt.year.toLong() * 1_0000_000_000L +
                                dt.monthNumber.toLong() * 1_000_000_00L +
                                dt.dayOfMonth.toLong() * 1_000_000L +
                                dt.hour.toLong() * 10_000L +
                                dt.minute.toLong() * 100L +
                                dt.second.toLong()
                        }
                }
            }
        timelineItems = merged
    }

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
                    TransactionTimeline(
                        items = timelineItems,
                        onTransactionClick = onTransactionClick,
                        onMarkerToggle = { sessionId ->
                            expandedMarkers =
                                if (sessionId in expandedMarkers) {
                                    expandedMarkers - sessionId
                                } else {
                                    expandedMarkers + sessionId
                                }
                            timelineItems =
                                timelineItems.map { item ->
                                    when {
                                        item is TransactionListItem.StartOverMarker &&
                                            item.session.id == sessionId ->
                                            item.copy(isExpanded = sessionId in expandedMarkers)
                                        else -> item
                                    }
                                }
                        },
                        onMarkerEdit = {},
                        onMarkerDelete = {},
                        modifier = Modifier.weight(1f),
                    )
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
