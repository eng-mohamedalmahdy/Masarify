package tech.lightfeather.masarify.template.transactionspane

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.CompactPaginationControls
import tech.lightfeather.designsystem.component.molecules.EmptyState
import tech.lightfeather.designsystem.component.molecules.button.FloatingActionButton
import tech.lightfeather.designsystem.component.organisms.TransactionTimeline
import tech.lightfeather.designsystem.model.TransactionListItem
import tech.lightfeather.designsystem.model.UiTransaction
import tech.lightfeather.designsystem.model.UiTransactionFilter
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.usecase.GetAllFinancialSessions
import tech.lightfeather.masarify.mappers.toTransactionFilter
import tech.lightfeather.masarify.mappers.toUiFinancialSession

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
actual fun TransactionsPane(
    title: String,
    filter: UiTransactionFilter?,
    viewModel: TransactionsPaneViewModel,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit,
    onTransactionClick: (UiTransaction) -> Unit,
    topBarSupportingContent: @Composable () -> Unit,
) {
    val getAllFinancialSessions = koinInject<GetAllFinancialSessions>()

    // Update filter when it changes
    LaunchedEffect(filter) {
        val domainFilter =
            filter?.toTransactionFilter() ?: tech.lightfeather.domain.model.transaction.TransactionFilter.EMPTY
        viewModel.updateFilter(domainFilter)
    }

    val transactions by viewModel.transactions.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()

    val isEmpty = transactions.data.isEmpty()
    val isFiltered = filter != null && !filter.isEmpty()

    var timelineItems by remember { mutableStateOf<List<TransactionListItem>>(emptyList()) }
    var expandedMarkers by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(transactions) {
        val sessions =
            when (val result = getAllFinancialSessions()) {
                is DomainResult.Success ->
                    result.data.let { flow ->
                        val collected = mutableListOf<tech.lightfeather.domain.model.FinancialSession>()
                        flow.collect { collected.addAll(it) }
                        collected
                    }
                is DomainResult.Failure -> emptyList()
            }

        val transactionItems = transactions.data.map { TransactionListItem.TransactionEntry(it) }
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
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
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
                        modifier = Modifier.weight(1f),
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

            CompactPaginationControls(
                currentPage = currentPage + 1,
                totalPages = transactions.totalPages,
                onPageChange = viewModel::setPage,
                onPreviousPage = viewModel::previousPage,
                onNextPage = viewModel::nextPage,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        FloatingActionButton(
            onClick = onAddClick,
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(MR.strings.add_account),
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(AppTheme.dimens.default)
                    .padding(bottom = AppTheme.dimens.massive),
        )
    }
}
