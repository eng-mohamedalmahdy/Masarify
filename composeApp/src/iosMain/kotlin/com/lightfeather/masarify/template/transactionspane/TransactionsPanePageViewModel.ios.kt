package com.lightfeather.masarify.template.transactionspane

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.usecase.GetAllTransactionsPaged
import com.lightfeather.domain.usecase.GetTransactionCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

actual class TransactionsPanePageViewModel(
    getAllTransactionsPaged: GetAllTransactionsPaged,
    getTransactionCount: GetTransactionCount,
    private val transactionsPagingSourceFactory: () -> TransactionsPagingSource,
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
        private const val INITIAL_LOAD_SIZE = 40
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val pager = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            initialLoadSize = INITIAL_LOAD_SIZE,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = transactionsPagingSourceFactory
    )

    val transactions: Flow<PagingData<UiTransaction>> = pager.flow
        .map { pagingData ->
            pagingData.map { transaction ->
                transaction.toUiTransaction()
            }
        }
        .cachedIn(viewModelScope)

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

    private fun Transaction.toUiTransaction(): UiTransaction {
        val type = when (this) {
            is Transaction.Income -> UiTransactionType.INCOME
            is Transaction.Expense -> UiTransactionType.EXPENSE
            is Transaction.Transfer -> UiTransactionType.TRANSFER
        }

        val category = when (this) {
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
            hasAttachment = this.attachments.isNotEmpty()
        )
    }

    private fun com.lightfeather.domain.model.Category.toUiCategory(): UiCategory {
        return UiCategory(
            id = this.id.toString(),
            name = this.name,
            color = this.color,
            image = this.logoUrl
        )
    }

    private fun formatAmount(amount: Double, type: UiTransactionType): String {
        val prefix = when (type) {
            UiTransactionType.INCOME -> "+"
            UiTransactionType.EXPENSE -> "-"
            UiTransactionType.TRANSFER -> ""
        }
        return "$prefix$%.2f".format(amount)
    }
}
