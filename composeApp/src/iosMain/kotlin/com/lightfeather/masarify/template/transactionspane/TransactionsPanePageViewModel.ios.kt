package com.lightfeather.masarify.template.transactionspane

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.usecase.GetAllTransactionsPaged
import com.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import com.lightfeather.domain.usecase.GetTransactionCount
import com.lightfeather.masarify.mappers.toTransactionFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

actual class TransactionsPanePageViewModel(
    getAllTransactionsPaged: GetAllTransactionsPaged,
    private val getFilteredTransactionsPaged: GetFilteredTransactionsPaged,
    getTransactionCount: GetTransactionCount,
    private val transactionsPagingSourceFactory: () -> TransactionsPagingSource,
    private val sharedDatabase: com.lightfeather.data.local.database.drivers.SharedDatabase,
) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
        private const val INITIAL_LOAD_SIZE = 40
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _currentFilter = MutableStateFlow(UiTransactionFilter.EMPTY)
    private val _isEmpty = MutableStateFlow(true)
    private val _isFiltered = MutableStateFlow(false)

    actual override val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()
    actual override val isFiltered: StateFlow<Boolean> = _isFiltered.asStateFlow()

    val transactions: Flow<PagingData<UiTransaction>> =
        _currentFilter
            .map { filter ->
                Pager(
                    config =
                        PagingConfig(
                            pageSize = PAGE_SIZE,
                            prefetchDistance = PREFETCH_DISTANCE,
                            initialLoadSize = INITIAL_LOAD_SIZE,
                            enablePlaceholders = false,
                        ),
                    pagingSourceFactory = {
                        TransactionsPagingSource(
                            sharedDatabase = sharedDatabase,
                            getFilteredTransactionsPaged = getFilteredTransactionsPaged,
                            filter = filter.toTransactionFilter(),
                        )
                    },
                ).flow
            }.flatMapLatest { it }
            .map { pagingData ->
                pagingData.map { transaction ->
                    transaction.toUiTransaction()
                }
            }.cachedIn(viewModelScope)

    fun refresh() {
        _isRefreshing.value = true
        // Paging3 will handle the actual refresh through invalidation
        // The isRefreshing state will be reset when new data loads
    }

    fun retry() {
        // Retry failed loads through Paging3
    }

    fun onTransactionClick(transactionId: String) {
        // Handle transaction selection
    }

    actual fun updateFilter(filter: UiTransactionFilter) {
        _currentFilter.value = filter
        _isFiltered.value = !filter.isEmpty()
        // The reactive flow will automatically update with the new filter
    }

    fun updateEmptyState(isEmpty: Boolean) {
        _isEmpty.value = isEmpty
    }

    private fun Transaction.toUiTransaction(): UiTransaction {
        val type =
            when (this) {
                is Transaction.Income -> UiTransactionType.INCOME
                is Transaction.Expense -> UiTransactionType.EXPENSE
                is Transaction.Transfer -> UiTransactionType.TRANSFER
            }

        val category =
            when (this) {
                is Transaction.Income -> this.source.toUiCategory()
                is Transaction.Expense -> this.categories.firstOrNull()?.toUiCategory() ?: UiCategory.dummy
                is Transaction.Transfer -> UiCategory.dummy
            }

        return UiTransaction(
            id = this.id.toString(),
            type = type,
            amount = formatAmount(this.amount, type),
            dateTime = Instant.fromEpochMilliseconds(this.timestamp).toLocalDateTime(TimeZone.currentSystemDefault()),
            description = this.name,
            category = category,
            hasAttachment = this.attachments.isNotEmpty(),
        )
    }

    private fun com.lightfeather.domain.model.Category.toUiCategory(): UiCategory =
        UiCategory(
            id = this.id.toString(),
            name = this.name,
            color = this.color,
            image = this.logoUrl,
        )

    private fun formatAmount(
        amount: Double,
        type: UiTransactionType,
    ): String {
        val prefix =
            when (type) {
                UiTransactionType.INCOME -> "+"
                UiTransactionType.EXPENSE -> "-"
                UiTransactionType.TRANSFER -> ""
            }
        return "$prefix$%.2f".format(amount)
    }
}
