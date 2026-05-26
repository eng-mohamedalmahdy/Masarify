package tech.lightfeather.masarify.page.transactions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import tech.lightfeather.designsystem.model.PageSize
import tech.lightfeather.designsystem.model.SavedFilter
import tech.lightfeather.designsystem.model.UiAttachment
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.model.UiCategory
import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.designsystem.model.UiTransaction
import tech.lightfeather.designsystem.model.UiTransactionDetails
import tech.lightfeather.designsystem.model.UiTransactionFilter

/**
 * State for the Transactions Page
 * Manages transaction list, filtering, pagination, and detail pane state
 */
data class TransactionsPageState(
    val selectedTransaction: UiTransaction? = null,
    // Filter state
    val filter: UiTransactionFilter = UiTransactionFilter.EMPTY,
    val savedFilters: List<SavedFilter> = emptyList(),
    val showFilterDialog: Boolean = false,
    // Pagination state
    val currentPage: Int = 0,
    val pageSize: PageSize = PageSize.DEFAULT,
    val totalCount: Long = 0,
    // Add/Edit transaction pane state
    val editingTransaction: UiTransactionDetails? = null,
    val lockedFromAccount: UiBankAccount? = null,
    val initialCategory: UiCategory? = null,
    val defaultAccount: UiBankAccount? = null,
    // Reference data
    val accounts: Flow<List<UiBankAccount>> = emptyFlow(),
    val categories: Flow<List<UiCategory>> = emptyFlow(),
    val currencies: Flow<List<UiCurrency>> = emptyFlow(),
    // Wealth header state
    val userAccountsCurrencies: Flow<List<UiCurrency>> = emptyFlow(),
    val defaultCurrency: Flow<UiCurrency?> = emptyFlow(),
    val selectedCurrency: UiCurrency? = null,
    val totalAmountInSelectedOrDefaultCurrency: String = "0.0",
    // Attachment state
    val selectedAttachments: List<UiAttachment> = emptyList(),
    val transactionAttachments: Map<String, List<UiAttachment>> = emptyMap(),
    // Loading state
    val isLoading: Boolean = false,
) {
    /**
     * Calculate total pages based on page size and total count
     */
    val totalPages: Int
        get() = if (totalCount == 0L) 0 else ((totalCount + pageSize.value - 1) / pageSize.value).toInt()

    /**
     * Check if there is a next page
     */
    val hasNextPage: Boolean
        get() = currentPage < totalPages - 1

    /**
     * Check if there is a previous page
     */
    val hasPreviousPage: Boolean
        get() = currentPage > 0

    /**
     * Check if filters are active
     */
    val hasActiveFilters: Boolean
        get() = !filter.isEmpty()
}
