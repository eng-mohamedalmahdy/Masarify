package com.lightfeather.masarify.template.transactionspane

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.model.transaction.TransactionFilter
import com.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import kotlinx.coroutines.flow.first

class TransactionsPagingSource(
    private val sharedDatabase: SharedDatabase,
    private val getFilteredTransactionsPaged: GetFilteredTransactionsPaged,
    private val filter: TransactionFilter,
) : PagingSource<Int, Transaction>() {
    companion object {
        const val PAGE_SIZE = 20
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        return try {
            val page = params.key ?: 0

            val result = getFilteredTransactionsPaged(filter, page)
            result.foldResult(
                onSuccess = { pagedDataFlow ->
                    // For PagingSource, we need to collect the first emission
                    val transactions: List<Transaction> = pagedDataFlow.first().data

                    LoadResult.Page(
                        data = transactions,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (transactions.size < PAGE_SIZE) null else page + 1,
                    )
                },
                onFailure = { exception ->
                    LoadResult.Error(Exception(exception.message))
                },
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {
        // Try to find the page key of the closest page to the most recently accessed index.
        // This will be the key passed to load after the PagingSource is invalidated.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
