package com.lightfeather.domain.model.transaction

import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.Currency
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Comprehensive transaction filter with fluent DSL builder
 * Supports filtering by all transaction properties including accounts, categories, currencies,
 * amount ranges, date ranges, transaction types, text search, and attachments.
 */
data class TransactionFilter(
    val ids: Set<Int> = emptySet(),
    val accounts: Set<Account> = emptySet(),
    val categories: Set<Category> = emptySet(),
    val currencies: Set<Currency> = emptySet(),
    val transactionTypes: Set<TransactionType> = emptySet(),
    val amountRange: AmountRange? = null,
    val dateRange: DateRange? = null,
    val textSearch: String? = null,
    val hasAttachments: Boolean? = null,
    val logic: FilterLogic = FilterLogic.AND,
) {
    /**
     * Amount range filter
     */
    data class AmountRange(
        val min: Double? = null,
        val max: Double? = null,
    ) {
        init {
            require(min == null || min >= 0) { "Minimum amount must be non-negative" }
            require(max == null || max >= 0) { "Maximum amount must be non-negative" }
            require(min == null || max == null || min <= max) { "Minimum amount must be less than or equal to maximum" }
        }
    }

    /**
     * Date range filter
     */
    data class DateRange(
        val from: LocalDateTime? = null,
        val to: LocalDateTime? = null,
    ) {
        init {
            require(from == null || to == null || from <= to) { "From date must be before or equal to to date" }
        }
    }

    /**
     * Transaction type for filtering
     */
    enum class TransactionType {
        INCOME,
        EXPENSE,
        TRANSFER,
    }

    /**
     * Filter logic - AND or OR
     */
    enum class FilterLogic {
        AND,
        OR,
    }

    /**
     * Check if this filter is empty (no criteria set)
     */
    fun isEmpty(): Boolean =
        ids.isEmpty() &&
            accounts.isEmpty() &&
            categories.isEmpty() &&
            currencies.isEmpty() &&
            transactionTypes.isEmpty() &&
            amountRange == null &&
            dateRange == null &&
            textSearch.isNullOrBlank() &&
            hasAttachments == null

    /**
     * Apply this filter to a transaction
     */
    @OptIn(ExperimentalTime::class)
    fun matches(transaction: Transaction): Boolean {
        if (isEmpty()) return true

        val criteria =
            buildList {
                // ID filter
                if (ids.isNotEmpty()) {
                    add(transaction.id in ids)
                }

                // Account filter
                if (accounts.isNotEmpty()) {
                    add(transaction.account in accounts)
                }

                // Category filter
                if (categories.isNotEmpty()) {
                    val transactionCategories =
                        when (transaction) {
                            is Transaction.Income -> setOf(transaction.source)
                            is Transaction.Expense -> transaction.categories.toSet()
                            is Transaction.Transfer -> emptySet()
                        }
                    add(categories.any { it in transactionCategories })
                }

                // Currency filter
                if (currencies.isNotEmpty()) {
                    add(transaction.account.currency in currencies)
                }

                // Transaction type filter
                if (transactionTypes.isNotEmpty()) {
                    val transactionType =
                        when (transaction) {
                            is Transaction.Income -> TransactionType.INCOME
                            is Transaction.Expense -> TransactionType.EXPENSE
                            is Transaction.Transfer -> TransactionType.TRANSFER
                        }
                    add(transactionType in transactionTypes)
                }

                // Amount range filter
                amountRange?.let { range ->
                    var matches = true
                    range.min?.let { min -> matches = matches && transaction.amount >= min }
                    range.max?.let { max -> matches = matches && transaction.amount <= max }
                    add(matches)
                }

                // Date range filter
                dateRange?.let { range ->
                    // Convert timestamp to LocalDateTime for comparison
                    val transactionDateTime =
                        kotlinx.datetime.Instant
                            .fromEpochMilliseconds(transaction.timestamp)
                            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                    var matches = true
                    range.from?.let { from -> matches = matches && transactionDateTime >= from }
                    range.to?.let { to -> matches = matches && transactionDateTime <= to }
                    add(matches)
                }

                // Text search filter
                textSearch?.takeIf { it.isNotBlank() }?.let { search ->
                    val searchLower = search.lowercase()
                    val nameMatch = transaction.name.lowercase().contains(searchLower)
                    val descMatch = transaction.description?.lowercase()?.contains(searchLower) ?: false
                    add(nameMatch || descMatch)
                }

                // Attachment filter
                hasAttachments?.let { shouldHave ->
                    add((transaction.attachments.isNotEmpty()) == shouldHave)
                }
            }

        return when (logic) {
            FilterLogic.AND -> criteria.all { it }
            FilterLogic.OR -> criteria.any { it }
        }
    }

    companion object {
        /**
         * Empty filter that matches all transactions
         */
        val EMPTY = TransactionFilter()
    }
}

/**
 * DSL builder for creating transaction filters with fluent API
 */
class TransactionFilterBuilder {
    private var ids = mutableSetOf<Int>()
    private var accounts = mutableSetOf<Account>()
    private var categories = mutableSetOf<Category>()
    private var currencies = mutableSetOf<Currency>()
    private var transactionTypes = mutableSetOf<TransactionFilter.TransactionType>()
    private var amountRange: TransactionFilter.AmountRange? = null
    private var dateRange: TransactionFilter.DateRange? = null
    private var textSearch: String? = null
    private var hasAttachments: Boolean? = null
    private var logic: TransactionFilter.FilterLogic = TransactionFilter.FilterLogic.AND

    /**
     * Filter by specific transaction IDs
     */
    fun idIn(vararg ids: Int) =
        apply {
            this.ids.addAll(ids.toList())
        }

    fun idIn(ids: Collection<Int>) =
        apply {
            this.ids.addAll(ids)
        }

    /**
     * Filter by accounts
     */
    fun accountIn(vararg accounts: Account) =
        apply {
            this.accounts.addAll(accounts)
        }

    fun accountIn(accounts: Collection<Account>) =
        apply {
            this.accounts.addAll(accounts)
        }

    /**
     * Filter by categories
     */
    fun categoryIn(vararg categories: Category) =
        apply {
            this.categories.addAll(categories)
        }

    fun categoryIn(categories: Collection<Category>) =
        apply {
            this.categories.addAll(categories)
        }

    /**
     * Filter by currencies
     */
    fun currencyIn(vararg currencies: Currency) =
        apply {
            this.currencies.addAll(currencies)
        }

    fun currencyIn(currencies: Collection<Currency>) =
        apply {
            this.currencies.addAll(currencies)
        }

    /**
     * Filter by transaction types
     */
    fun transactionTypeIn(vararg types: TransactionFilter.TransactionType) =
        apply {
            this.transactionTypes.addAll(types)
        }

    fun transactionTypeIn(types: Collection<TransactionFilter.TransactionType>) =
        apply {
            this.transactionTypes.addAll(types)
        }

    // Convenience methods for transaction types
    fun incomeOnly() = transactionTypeIn(TransactionFilter.TransactionType.INCOME)

    fun expenseOnly() = transactionTypeIn(TransactionFilter.TransactionType.EXPENSE)

    fun transferOnly() = transactionTypeIn(TransactionFilter.TransactionType.TRANSFER)

    /**
     * Filter by amount range
     */
    fun amountBetween(
        min: Double,
        max: Double,
    ) = apply {
        this.amountRange = TransactionFilter.AmountRange(min, max)
    }

    fun amountGreaterThan(min: Double) =
        apply {
            this.amountRange = TransactionFilter.AmountRange(min = min)
        }

    fun amountLessThan(max: Double) =
        apply {
            this.amountRange = TransactionFilter.AmountRange(max = max)
        }

    fun amountExactly(amount: Double) =
        apply {
            this.amountRange = TransactionFilter.AmountRange(amount, amount)
        }

    /**
     * Filter by date range
     */
    fun dateRange(
        from: LocalDateTime,
        to: LocalDateTime,
    ) = apply {
        this.dateRange = TransactionFilter.DateRange(from, to)
    }

    fun dateAfter(from: LocalDateTime) =
        apply {
            this.dateRange = TransactionFilter.DateRange(from = from)
        }

    fun dateBefore(to: LocalDateTime) =
        apply {
            this.dateRange = TransactionFilter.DateRange(to = to)
        }

    fun dateExactly(date: LocalDateTime) =
        apply {
            this.dateRange = TransactionFilter.DateRange(date, date)
        }

    /**
     * Text search in name and description
     */
    fun textContains(text: String) =
        apply {
            this.textSearch = text
        }

    /**
     * Filter by attachment presence
     */
    fun hasAttachments() =
        apply {
            this.hasAttachments = true
        }

    fun noAttachments() =
        apply {
            this.hasAttachments = false
        }

    /**
     * Set filter logic
     */
    fun useAndLogic() =
        apply {
            this.logic = TransactionFilter.FilterLogic.AND
        }

    fun useOrLogic() =
        apply {
            this.logic = TransactionFilter.FilterLogic.OR
        }

    /**
     * Build the final filter
     */
    fun build(): TransactionFilter =
        TransactionFilter(
            ids = ids.toSet(),
            accounts = accounts.toSet(),
            categories = categories.toSet(),
            currencies = currencies.toSet(),
            transactionTypes = transactionTypes.toSet(),
            amountRange = amountRange,
            dateRange = dateRange,
            textSearch = textSearch,
            hasAttachments = hasAttachments,
            logic = logic,
        )
}

/**
 * DSL function for creating transaction filters
 */
fun transactionFilter(block: TransactionFilterBuilder.() -> Unit): TransactionFilter =
    TransactionFilterBuilder().apply(block).build()
