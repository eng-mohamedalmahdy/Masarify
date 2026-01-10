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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

actual class TransactionsPaneViewModel(
    private val filter: TransactionFilter,
    private val getFilteredTransactions: GetFilteredTransactionsPaged,
) : ViewModel() {

    val transactions: MutableStateFlow<PagedData<UiTransaction>> = MutableStateFlow(PagedData.empty())

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    init {
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            _currentPage.collect { page ->
                val transactionsResult = getFilteredTransactions(filter, page)
                transactionsResult.foldSuspend(
                    onSuccess = {
                        it.map { it.map { it.toUiTransaction() } }.collect(transactions)
                    },
                    onFailure = {
                        Napier.d { "Error fetching transactions: $it" }
                        SnackbarService.sendErrorMessage(MR.strings.unknown_error)
                    }
                )
            }
        }
    }

    fun nextPage() {
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            if (_currentPage.value < transactions.value.totalPages) {
                _currentPage.value++
            }
        }
    }

    fun previousPage() {
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            if (_currentPage.value > 1) {
                _currentPage.value--
            }
        }
    }

    fun setPage(page: Int) {
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            _currentPage.value = page
        }
    }
}
