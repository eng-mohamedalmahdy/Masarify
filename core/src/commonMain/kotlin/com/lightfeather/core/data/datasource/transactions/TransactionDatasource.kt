package com.lightfeather.core.data.datasource.transactions

import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter

sealed interface TransactionDatasource<T : Transaction> {

    suspend fun createTransaction(transaction: T)

    suspend fun updateTransaction(transaction: T)

    suspend fun deleteTransaction(transaction: T): Boolean

    suspend fun getAllTransactions(): List<T>

    suspend fun getTransactionById(id: Int): T

    suspend fun getMinTransaction(): T

    suspend fun getMaxTransaction(): T

    suspend fun getAverageTransactionValue(): Double

    suspend fun getFilteredTransactions(transactions: List<T>, filter: TransactionFilter): List<T>

}