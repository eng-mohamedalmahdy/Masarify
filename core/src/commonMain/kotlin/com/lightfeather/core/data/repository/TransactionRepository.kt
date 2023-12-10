package com.lightfeather.core.data.repository

import com.lightfeather.core.data.datasource.transactions.TransactionDatasource
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter

abstract class TransactionRepository<T : Transaction>(private val datasource: TransactionDatasource<T>) {

    suspend fun createTransaction(transaction: T) = datasource.createTransaction(transaction)


    suspend fun deleteTransaction(transaction: Transaction) = datasource.deleteTransaction(transaction)

    suspend fun getAllTransactions() = datasource.getAllTransactions()

    suspend fun getTransactionById(id: Int) = datasource.getTransactionById(id)

    suspend fun getMinTransaction() = datasource.getMinTransaction()

    suspend fun getMaxTransaction() = datasource.getMaxTransaction()

    suspend fun getAverageTransactionValue() = datasource.getAverageTransactionValue()

    suspend fun getFilteredTransactions(transactions: List<T>, filter: TransactionFilter) =
        datasource.getFilteredTransactions(transactions, filter)

   suspend fun updateTransaction(newTransaction: T)  = datasource.updateTransaction(newTransaction)

}