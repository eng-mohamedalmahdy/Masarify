package com.lightfeather.domain.data.repository

import com.lightfeather.domain.domain.Category
import com.lightfeather.domain.domain.Currency
import com.lightfeather.domain.domain.DomainResult
import com.lightfeather.domain.domain.transaction.Transaction
import com.lightfeather.domain.domain.transaction.TransactionFilter
import kotlinx.coroutines.flow.Flow


interface TransactionRepository<T : Transaction> {

    suspend fun createTransaction(transaction: T): DomainResult<Int>


    suspend fun deleteTransaction(transaction: Transaction): DomainResult<Boolean>

    suspend fun getAllTransactions(): DomainResult<Flow<List<T>>>

    suspend fun getTransactionById(id: Int): DomainResult<T>

    suspend fun getMinTransaction():  DomainResult<T>

    suspend fun getMaxTransaction():  DomainResult<T>

    suspend fun getAverageTransactionValue(): DomainResult<Double>

    suspend fun getFilteredTransactions(transactions: List<T>, filter: TransactionFilter): DomainResult<List<T>>

    suspend fun updateTransaction(newTransaction: T): DomainResult<Boolean>

    suspend fun getTotalTransactionsOfCurrency(currency: Currency): DomainResult<Flow<Int>>

    suspend fun getTotalTransactionsOfCategories(): DomainResult<Flow<Map<Category, Int>>>
}