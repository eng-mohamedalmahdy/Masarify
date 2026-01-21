package com.lightfeather.masarify.template.transactionspane

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.data.util.IoDispatcher
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.domain.model.PagedData
import com.lightfeather.domain.model.transaction.TransactionFilter
import com.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import com.lightfeather.masarify.mappers.toUiTransaction
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class TransactionsPaneViewModel(
    private val filter: TransactionFilter,
    private val getFilteredTransactions: GetFilteredTransactionsPaged,
) : ViewModel() {

    val transactions: MutableStateFlow<PagedData<UiTransaction>> = MutableStateFlow(PagedData.empty())
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    init {
        Napier.d { "TransactionsPaneViewModel initialized with filter: $filter" }

        // Realtime update: Use flatMapLatest to switch to new Flow when page changes
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            _currentPage
                .flatMapLatest { page ->
                    Napier.d { "Fetching transactions for page: $page" }

                    val transactionsResult = getFilteredTransactions(filter, page)
                    transactionsResult.foldResult(
                        onSuccess = { pagedDataFlow ->
                            Napier.d { "Successfully got Flow<PagedData> for page: $page" }
                            pagedDataFlow.map { pagedData ->
                                Napier.d { "Mapping ${pagedData.data.size} transactions to UI models" }
                                pagedData.map { it.toUiTransaction() }
                            }
                        },
                        onFailure = { error ->
                            Napier.e { "Error fetching transactions for page $page" }
                            SnackbarService.sendErrorMessage(MR.strings.unknown_error)
                            emptyFlow()
                        }
                    )
                }.collect { value ->
                    withContext(Dispatchers.Main) {
                        Napier.d {
                            "Updating transactions StateFlow with ${value.data.size}" +
                                " items, totalPages=${value.totalPages}"
                        }
                        transactions.value = value
                    }
                }
        }
    }

    fun nextPage() {
        viewModelScope.launch(Dispatchers.Main) {
            if (_currentPage.value < transactions.value.totalPages - 1) {
                Napier.d { "Navigating to next page: ${_currentPage.value + 1}" }
                _currentPage.value++
            } else {
                Napier.d { "Already at last page: ${_currentPage.value}" }
            }
        }
    }

    fun previousPage() {
        viewModelScope.launch(Dispatchers.Main) {
            if (_currentPage.value > 0) {
                Napier.d { "Navigating to previous page: ${_currentPage.value - 1}" }
                _currentPage.value--
            } else {
                Napier.d { "Already at first page: ${_currentPage.value}" }
            }
        }
    }

    fun setPage(page: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            Napier.d { "Setting page explicitly to $page" }
            _currentPage.value = page
        }
    }
}
