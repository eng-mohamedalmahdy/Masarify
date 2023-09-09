package com.lightfeather.core.domain.transaction

import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Category

class TransactionFilter private constructor(
    private val filterAll: FilterAll, private val filterAny: FilterAny, private var id: Int? = null,
    private var name: String?,
    private var description: String?,
    private var categories: List<Category>,
    private var amount: Double?,
    private var timestampFrom: Int?,
    private var timestampTo: Int?,
    private var account: Account?,
) {


    fun filterAll(transaction: Transaction): Boolean = filterAll.filterAll(this, transaction)

    fun filterAny(transaction: Transaction): Boolean = filterAny.filterAny(this, transaction)

    class Builder(private val filterAll: FilterAll, private val filterAny: FilterAny) {

        private var id: Int? = null
        private var name: String? = null
        private var description: String? = null
        private var categories: List<Category> = listOf()
        private var amount: Double? = null
        private var timestampFrom: Int? = null
        private var timestampTo: Int? = null
        private var account: Account? = null

        fun id(id: Int?) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun description(description: String) = apply { this.description = description }
        fun categories(categories: List<Category>) = apply { this.categories = categories }
        fun amount(amount: Double) = apply { this.amount = amount }
        fun timestampFrom(timestampFrom: Int) = apply { this.timestampFrom = timestampFrom }
        fun timestampTo(timestampTo: Int) = apply { this.timestampTo = timestampTo }
        fun account(account: Account) = apply { this.account = account }

        fun build(): TransactionFilter = TransactionFilter(filterAll,
            filterAny,
            id,
            name,
            description,
            categories,
            amount,
            timestampFrom,
            timestampTo,
            account
        )
    }
}

fun interface FilterAll {
    fun filterAll(transactionFilter: TransactionFilter, transaction: Transaction): Boolean
}

fun interface FilterAny {
    fun filterAny(transactionFilter: TransactionFilter, transaction: Transaction): Boolean
}