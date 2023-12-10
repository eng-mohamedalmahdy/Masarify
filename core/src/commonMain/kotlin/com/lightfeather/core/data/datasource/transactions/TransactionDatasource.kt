package com.lightfeather.core.data.datasource.transactions

import com.lightfeather.core.domain.DomainResult
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter
import kotlinx.coroutines.flow.Flow

sealed interface TransactionDatasource<T : Transaction> {

    suspend fun createTransaction(transaction: T): DomainResult<Int>


    suspend fun deleteTransaction(transaction: Transaction): Boolean

    suspend fun getAllTransactions(): Flow<List<T>>

    suspend fun getTransactionById(id: Int): DomainResult<T>

    suspend fun getMinTransaction(): Flow<T>

    suspend fun getMaxTransaction(): Flow<T>

    suspend fun getAverageTransactionValue(): Flow<Double>

    suspend fun getFilteredTransactions(transactions: List<T>, filter: TransactionFilter): Flow<List<T>>

    suspend fun updateTransaction(transaction: T)

}