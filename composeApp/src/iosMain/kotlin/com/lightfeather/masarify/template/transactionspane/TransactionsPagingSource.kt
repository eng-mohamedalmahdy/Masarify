package com.lightfeather.masarify.template.transactionspane

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.cash.sqldelight.async.coroutines.awaitAsList
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.data.mapper.toDomainTransactions
import com.lightfeather.domain.model.transaction.Transaction

class TransactionsPagingSource(
    private val sharedDatabase: SharedDatabase,
) : PagingSource<Int, Transaction>() {

    companion object {
        const val PAGE_SIZE = 20
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        return try {
            val page = params.key ?: 0
            val offset = page * PAGE_SIZE

            val transactions = sharedDatabase {
                it.transactionsQueries
                    .getAllTransactionsPaged(limit = PAGE_SIZE.toLong(), offset = offset.toLong())
                    .awaitAsList()
                    .toDomainTransactions()
            }

            LoadResult.Page(
                data = transactions,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (transactions.size < PAGE_SIZE) null else page + 1,
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
