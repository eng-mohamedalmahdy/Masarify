package com.lightfeather.core.data.repository

import com.lightfeather.core.data.datasource.transactions.TransactionDatasource
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter

abstract class TransactionRepository<T : Transaction>(private val datasource: TransactionDatasource<T>) {

   suspend  fun createTransaction(transaction: T) = datasource.createTransaction(transaction)

   suspend  fun updateTransaction(transaction: T) = datasource.updateTransaction(transaction)

   suspend  fun deleteTransaction(transaction: T) = datasource.deleteTransaction(transaction)

   suspend  fun getAllTransactions(): List<T> = datasource.getAllTransactions()

   suspend  fun getTransactionById(id: Int): T = datasource.getTransactionById(id)

   suspend  fun getMinTransaction(): T = datasource.getMinTransaction()

   suspend  fun getMaxTransaction(): T = datasource.getMaxTransaction()

   suspend  fun getAverageTransactionValue(): Double = datasource.getAverageTransactionValue()

   suspend  fun getFilteredTransactions(transactions: List<T>, filter: TransactionFilter): List<T> =
        datasource.getFilteredTransactions(transactions, filter)

}