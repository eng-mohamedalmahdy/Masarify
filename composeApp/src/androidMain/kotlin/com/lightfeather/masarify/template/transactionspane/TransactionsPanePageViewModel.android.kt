package com.lightfeather.masarify.template.transactionspane

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.usecase.GetAllTransactionsPaged
import com.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import com.lightfeather.domain.usecase.GetTransactionCount
import com.lightfeather.masarify.mappers.toTransactionFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

actual class TransactionsPanePageViewModel(
    // Reserved for future pagination implementation
    @Suppress("UnusedPrivateProperty")
    getAllTransactionsPaged: GetAllTransactionsPaged,
    private val getFilteredTransactionsPaged: GetFilteredTransactionsPaged,
    // Reserved for future pagination implementation
    @Suppress("UnusedPrivateProperty")
    getTransactionCount: GetTransactionCount,
    // Reserved for future pagination implementation
    @Suppress("UnusedPrivateProperty")
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

    actual val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()
    actual val isFiltered: StateFlow<Boolean> = _isFiltered.asStateFlow()
    val pagerFlow =
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
                )
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: Flow<PagingData<UiTransaction>> =
        pagerFlow
            .flatMapLatest { it.flow }
            .map { pagingData ->
                pagingData.map { transaction -> transaction.toUiTransaction() }
            }.cachedIn(viewModelScope)

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(2000)
            _isRefreshing.value = false
        }
    }

    fun retry() {
        // Retry failed loads through Paging3
    }

    fun onTransactionClick(transactionId: String) {
     transactionId
    }

    actual fun updateFilter(filter: UiTransactionFilter) {
        _currentFilter.value = filter
        _isFiltered.value = !filter.isEmpty()
        // Invalidate paging source to trigger reload with new filter
        // This will be handled by the updated TransactionsPagingSource
    }

    fun updateEmptyState(isEmpty: Boolean) {
        _isEmpty.value = isEmpty
    }

    @OptIn(ExperimentalTime::class)
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

    private fun Category.toUiCategory(): UiCategory =
        UiCategory(
            id = this.id.toString(),
            name = this.name,
            color = this.color,
            image = this.icon,
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
