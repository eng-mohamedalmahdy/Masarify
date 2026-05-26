package tech.lightfeather.masarify.template.transactionspane

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import tech.lightfeather.domain.model.transaction.TransactionFilter
import tech.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import tech.lightfeather.masarify.mappers.toUiTransaction

actual class TransactionsPaneViewModel(
    initialFilter: TransactionFilter,
    val getFilteredTransactionsPaged: GetFilteredTransactionsPaged,
) : ViewModel() {
    // Reactive filter state
    private val _filterFlow = MutableStateFlow(initialFilter)

    /**
     * Update the filter and trigger recomposition of the paging flow
     * Uses flatMapLatest to recreate Pager when filter changes
     */
    actual fun updateFilter(filter: TransactionFilter) {
        _filterFlow.value = filter
    }

    // Reactive transactions flow that recreates Pager on filter change
    val transactions =
        _filterFlow
            .flatMapLatest { currentFilter ->
                Pager(
                    config = PagingConfig(pageSize = TransactionsPagingSource.PAGE_SIZE),
                    pagingSourceFactory = { TransactionsPagingSource(getFilteredTransactionsPaged, currentFilter) },
                ).flow
            }.map { pagingData ->
                pagingData.map { it.toUiTransaction() }
            }
}
