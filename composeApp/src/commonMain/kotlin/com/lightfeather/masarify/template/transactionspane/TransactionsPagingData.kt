package com.lightfeather.masarify.template.transactionspane

import com.lightfeather.domain.usecase.GetFilteredTransactionsPaged

/**
 * Data class to pass paging setup to Android/iOS pane implementations
 * Contains the necessary dependencies to create a paging flow
 *
 * @param getFilteredTransactionsPaged Use case for fetching paged transactions
 * @param sharedDatabase Platform-specific database instance (for Android/iOS PagingSource)
 */
data class TransactionsPagingData(
    val getFilteredTransactionsPaged: GetFilteredTransactionsPaged,
    val sharedDatabase: Any, // Platform-specific database type (SharedDatabase for Android/iOS)
)
