package com.lightfeather.designsystem.model

import kotlinx.datetime.LocalDateTime

/**
 * UI representation of transaction filter for use in composables
 * Follows the existing UI model patterns with string-based IDs and simplified structures
 */
data class UiTransactionFilter(
    val ids: Set<String> = emptySet(),
    val accounts: Set<UiBankAccount> = emptySet(),
    val categories: Set<UiCategory> = emptySet(),
    val currencies: Set<UiCurrency> = emptySet(),
    val transactionTypes: Set<UiTransactionType> = emptySet(),
    val amountRange: UiAmountRange? = null,
    val dateRange: UiDateRange? = null,
    val textSearch: String? = null,
    val hasAttachments: Boolean? = null,
    val logic: UiFilterLogic = UiFilterLogic.AND,
) {
    /**
     * UI amount range filter
     */
    data class UiAmountRange(
        val min: String? = null,
        val max: String? = null,
    ) {
        /**
         * Convert to double values, handling potential parsing errors
         */
        fun getMinValue(): Double? = min?.toDoubleOrNull()

        fun getMaxValue(): Double? = max?.toDoubleOrNull()

        /**
         * Check if range is valid
         */
        fun isValid(): Boolean {
            val minVal = getMinValue()
            val maxVal = getMaxValue()
            return when {
                minVal != null && minVal < 0 -> false
                maxVal != null && maxVal < 0 -> false
                minVal != null && maxVal != null && minVal > maxVal -> false
                else -> true
            }
        }
    }

    /**
     * UI date range filter
     */
    data class UiDateRange(
        val from: LocalDateTime? = null,
        val to: LocalDateTime? = null,
    ) {
        /**
         * Check if range is valid
         */
        fun isValid(): Boolean = from == null || to == null || from <= to
    }

    /**
     * UI filter logic
     */
    enum class UiFilterLogic {
        AND,
        OR,
        ;

        fun getDisplayName(): String =
            when (this) {
                AND -> "All conditions"
                OR -> "Any condition"
            }
    }

    /**
     * Check if this filter is empty (no criteria set)
     */
    fun isEmpty(): Boolean =
        (
            ids.isEmpty() &&
                accounts.isEmpty() &&
                categories.isEmpty() &&
                currencies.isEmpty() &&
                transactionTypes.isEmpty() &&
                amountRange == null &&
                dateRange == null &&
                textSearch.isNullOrBlank() &&
                hasAttachments == null
        ) ||
            this == EMPTY

    /**
     * Get a human-readable description of the filter
     */
    fun getDescription(): String {
        if (isEmpty()) return "No filters applied"

        val parts =
            buildList {
                addAccountsDescription()
                addCategoriesDescription()
                addCurrenciesDescription()
                addTransactionTypesDescription()
                addAmountRangeDescription()
                addDateRangeDescription()
                addTextSearchDescription()
                addAttachmentsDescription()
            }

        return parts.joinToString(
            separator = if (logic == UiFilterLogic.AND) " AND " else " OR ",
        )
    }

    private fun MutableList<String>.addAccountsDescription() {
        if (accounts.isNotEmpty()) {
            add("Accounts: ${accounts.joinToString(", ") { it.name }}")
        }
    }

    private fun MutableList<String>.addCategoriesDescription() {
        if (categories.isNotEmpty()) {
            add("Categories: ${categories.joinToString(", ") { it.name }}")
        }
    }

    private fun MutableList<String>.addCurrenciesDescription() {
        if (currencies.isNotEmpty()) {
            add("Currencies: ${currencies.joinToString(", ") { it.symbol }}")
        }
    }

    private fun MutableList<String>.addTransactionTypesDescription() {
        if (transactionTypes.isNotEmpty()) {
            add("Types: ${transactionTypes.joinToString(", ") { it.name }}")
        }
    }

    private fun MutableList<String>.addAmountRangeDescription() {
        amountRange?.let { range ->
            val description =
                when {
                    range.min != null && range.max != null -> "Amount: ${range.min} - ${range.max}"
                    range.min != null -> "Amount: >= ${range.min}"
                    range.max != null -> "Amount: <= ${range.max}"
                    else -> null
                }
            description?.let { add(it) }
        }
    }

    private fun MutableList<String>.addDateRangeDescription() {
        dateRange?.let { range ->
            val description =
                when {
                    range.from != null && range.to != null -> "Date: ${range.from} to ${range.to}"
                    range.from != null -> "Date: from ${range.from}"
                    range.to != null -> "Date: until ${range.to}"
                    else -> null
                }
            description?.let { add(it) }
        }
    }

    private fun MutableList<String>.addTextSearchDescription() {
        textSearch?.takeIf { it.isNotBlank() }?.let {
            add("Search: \"$it\"")
        }
    }

    private fun MutableList<String>.addAttachmentsDescription() {
        hasAttachments?.let { hasAttach ->
            add(if (hasAttach) "With attachments" else "Without attachments")
        }
    }

    /**
     * Get the number of active filter criteria
     */
    fun getActiveFilterCount(): Int {
        var count = 0
        if (ids.isNotEmpty()) count++
        if (accounts.isNotEmpty()) count++
        if (categories.isNotEmpty()) count++
        if (currencies.isNotEmpty()) count++
        if (transactionTypes.isNotEmpty()) count++
        if (amountRange != null) count++
        if (dateRange != null) count++
        if (!textSearch.isNullOrBlank()) count++
        if (hasAttachments != null) count++
        return count
    }

    companion object {
        /**
         * Empty filter that matches all transactions
         */
        val EMPTY = UiTransactionFilter()
    }
}

/**
 * DSL builder for creating UI transaction filters
 */
class UiTransactionFilterBuilder {
    private var ids = mutableSetOf<String>()
    private var accounts = mutableSetOf<UiBankAccount>()
    private var categories = mutableSetOf<UiCategory>()
    private var currencies = mutableSetOf<UiCurrency>()
    private var transactionTypes = mutableSetOf<UiTransactionType>()
    private var amountRange: UiTransactionFilter.UiAmountRange? = null
    private var dateRange: UiTransactionFilter.UiDateRange? = null
    private var textSearch: String? = null
    private var hasAttachments: Boolean? = null
    private var logic: UiTransactionFilter.UiFilterLogic = UiTransactionFilter.UiFilterLogic.AND

    fun idIn(vararg ids: String) =
        apply {
            this.ids.addAll(ids)
        }

    fun accountIn(vararg accounts: UiBankAccount) =
        apply {
            this.accounts.addAll(accounts)
        }

    fun categoryIn(vararg categories: UiCategory) =
        apply {
            this.categories.addAll(categories)
        }

    fun currencyIn(vararg currencies: UiCurrency) =
        apply {
            this.currencies.addAll(currencies)
        }

    fun transactionTypeIn(vararg types: UiTransactionType) =
        apply {
            this.transactionTypes.addAll(types)
        }

    fun amountBetween(
        min: String?,
        max: String?,
    ) = apply {
        this.amountRange = UiTransactionFilter.UiAmountRange(min, max)
    }

    fun dateRange(
        from: LocalDateTime?,
        to: LocalDateTime?,
    ) = apply {
        this.dateRange = UiTransactionFilter.UiDateRange(from, to)
    }

    fun textContains(text: String?) =
        apply {
            this.textSearch = text
        }

    fun hasAttachments(has: Boolean?) =
        apply {
            this.hasAttachments = has
        }

    fun useAndLogic() =
        apply {
            this.logic = UiTransactionFilter.UiFilterLogic.AND
        }

    fun useOrLogic() =
        apply {
            this.logic = UiTransactionFilter.UiFilterLogic.OR
        }

    fun build(): UiTransactionFilter =
        UiTransactionFilter(
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
 * DSL function for creating UI transaction filters
 */
fun uiTransactionFilter(block: UiTransactionFilterBuilder.() -> Unit): UiTransactionFilter =
    UiTransactionFilterBuilder().apply(block).build()
