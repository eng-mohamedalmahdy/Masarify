package com.lightfeather.masarify.template.transactionspane

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.lightfeather.domain.model.transaction.TransactionFilter
import com.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import com.lightfeather.masarify.mappers.toUiTransaction
import kotlinx.coroutines.flow.map

actual class TransactionsPaneViewModel(
    val filter: TransactionFilter,
    val getFilteredTransactionsPaged: GetFilteredTransactionsPaged,
) : ViewModel() {

    val transactions = Pager(
        config = PagingConfig(pageSize = TransactionsPagingSource.PAGE_SIZE),
        pagingSourceFactory = { TransactionsPagingSource(getFilteredTransactionsPaged, filter) }
    ).flow.map { it.map { it.toUiTransaction() } }
}
