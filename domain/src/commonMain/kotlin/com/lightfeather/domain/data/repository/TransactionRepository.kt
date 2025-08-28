package com.lightfeather.domain.data.repository

import com.lightfeather.domain.domain.Category
import com.lightfeather.domain.domain.Currency
import com.lightfeather.domain.domain.transaction.Transaction
import com.lightfeather.domain.domain.transaction.TransactionFilter
import kotlinx.coroutines.flow.Flow


interface TransactionRepository<T : Transaction> {

    suspend fun createTransaction(transaction: T): Boolean


    suspend fun deleteTransaction(transaction: Transaction): Boolean

    suspend fun getAllTransactions(): Flow<List<T>>

    suspend fun getTransactionById(id: Int): T?

    suspend fun getMinTransaction(): T?

    suspend fun getMaxTransaction(): T?

    suspend fun getAverageTransactionValue(): Double

    suspend fun getFilteredTransactions(transactions: List<T>, filter: TransactionFilter): List<T>

    suspend fun updateTransaction(newTransaction: T): Boolean

    suspend fun getTotalTransactionsOfCurrency(currency: Currency): Flow<Int>

    suspend fun getTotalTransactionsOfCategories(): Flow<Map<Category, Int>>
}