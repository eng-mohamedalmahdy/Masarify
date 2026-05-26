package tech.lightfeather.masarify.template.transactionspane

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.domain.model.PagedData
import tech.lightfeather.domain.model.transaction.Transaction
import tech.lightfeather.domain.model.transaction.TransactionFilter
import tech.lightfeather.domain.usecase.GetFilteredTransactionsPaged

/**
 * PagingSource that supports realtime database updates.
 * When the database changes, it invalidates itself to trigger a refresh.
 */
class TransactionsPagingSource(
    private val getFilteredTransactionsPaged: GetFilteredTransactionsPaged,
    private val filter: TransactionFilter,
) : PagingSource<Int, Transaction>() {
    companion object {
        const val PAGE_SIZE = 20
    }

    private val scope = CoroutineScope(Dispatchers.IoDispatcher + Job())
    private var observerJob: Job? = null

    init {
        // Observe the first page for database changes to trigger invalidation
        observerJob =
            scope.launch {
                val result = getFilteredTransactionsPaged(filter, 0)
                result.foldResult(
                    onSuccess = { pagedDataFlow ->
                        var previousData: PagedData<Transaction>? = null
                        pagedDataFlow.collect { pagedData ->
                            // Invalidate if data changed (excluding first emission)
                            if (previousData != null && previousData != pagedData) {
                                invalidate()
                            }
                            previousData = pagedData
                        }
                    },
                    onFailure = { /* Ignore errors in observer */ },
                )
            }
    }

    // Paging library requires catching all exceptions to return LoadResult.Error
    @Suppress("TooGenericExceptionCaught")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> =
        try {
            val page = params.key ?: 0

            val result = getFilteredTransactionsPaged(filter, page)
            result.foldResult(
                onSuccess = { pagedDataFlow ->
                    // Get current data snapshot for this page
                    val pagedData = pagedDataFlow.first()

                    LoadResult.Page(
                        data = pagedData.data,
                        prevKey = if (page == 0) null else page - 1,
                        nextKey = if (!pagedData.hasNextPage) null else page + 1,
                    )
                },
                onFailure = { exception ->
                    LoadResult.Error(Exception(exception.message))
                },
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }

    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {
        // Try to find the page key of the closest page to the most recently accessed index.
        // This will be the key passed to load after the PagingSource is invalidated.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override val jumpingSupported: Boolean = true
}
