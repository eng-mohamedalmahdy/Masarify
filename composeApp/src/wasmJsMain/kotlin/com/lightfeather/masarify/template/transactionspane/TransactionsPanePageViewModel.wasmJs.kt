package com.lightfeather.masarify.template.transactionspane

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.PagedData
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.usecase.GetAllTransactionsPaged
import com.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import com.lightfeather.domain.usecase.GetTransactionCount
import com.lightfeather.masarify.mappers.toTransactionFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

actual class TransactionsPanePageViewModel(
    private val getAllTransactionsPaged: GetAllTransactionsPaged,
    private val getFilteredTransactionsPaged: GetFilteredTransactionsPaged,
    private val getTransactionCount: GetTransactionCount,
) : ViewModel() {
    private val _transactions = MutableStateFlow<List<UiTransaction>>(emptyList())
    val transactions: StateFlow<List<UiTransaction>> = _transactions.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentFilter = MutableStateFlow(UiTransactionFilter.EMPTY)
    private val _isEmpty = MutableStateFlow(true)
    private val _isFiltered = MutableStateFlow(false)

    actual  val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()
    actual  val isFiltered: StateFlow<Boolean> = _isFiltered.asStateFlow()

    private val _pagedTransactions = MutableStateFlow(PagedData.empty<Transaction>())

    init {
        loadTransactions(0)
    }

    fun goToPage(page: Int) {
        val current = _pagedTransactions.value
        val zeroBasedPage = page - 1 // Convert to 0-based indexing
        if (zeroBasedPage in 0 until current.totalPages) {
            loadTransactions(zeroBasedPage)
        }
    }

    fun nextPage() {
        val current = _pagedTransactions.value
        if (current.hasNextPage) {
            loadTransactions(current.page + 1)
        }
    }

    fun previousPage() {
        val current = _pagedTransactions.value
        if (current.hasPreviousPage) {
            loadTransactions(current.page - 1)
        }
    }

    fun refresh() {
        val currentPage = _currentPage.value - 1 // Convert to 0-based indexing
        loadTransactions(currentPage)
    }

    fun onTransactionClick(transactionId: String) {
        // Handle transaction selection
    }

    actual fun updateFilter(filter: UiTransactionFilter) {
        _currentFilter.value = filter
        _isFiltered.value = !filter.isEmpty()
        refresh() // Reload with new filter
    }

    private fun loadTransactions(page: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val filter = _currentFilter.value.toTransactionFilter()
            val result = getFilteredTransactionsPaged(filter, page)
            result.foldSuspend(
                onSuccess = { pagedDataFlow ->
                    pagedDataFlow.collectLatest { pagedData ->
                        _pagedTransactions.value = pagedData
                        _transactions.value = pagedData.data.map { it.toUiTransaction() }
                        _currentPage.value = pagedData.page + 1 // Convert to 1-based indexing
                        _totalPages.value = pagedData.totalPages
                        _isEmpty.value = pagedData.data.isEmpty()
                        _isLoading.value = false
                        _error.value = null
                    }
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load transactions"
                    _isLoading.value = false
                },
            )
        }
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
        return "$prefix$amount"
    }
}
