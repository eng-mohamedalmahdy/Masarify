package data.repository

import data.datasource.transactions.TransactionDatasource
import domain.transaction.Transaction
import domain.transaction.TransactionFilter

abstract class TransactionRepository<T : Transaction>(private val datasource: TransactionDatasource<T>) {

    fun createTransaction(transaction: T) = datasource.createTransaction(transaction)

    fun updateTransaction(transaction: T) = datasource.updateTransaction(transaction)

    fun deleteTransaction(transaction: T) = datasource.deleteTransaction(transaction)

    fun getAllTransactions(): List<T> = datasource.getAllTransactions()

    fun getTransactionById(id: Int): T = datasource.getTransactionById(id)

    fun getMinTransaction(): T = datasource.getMinTransaction()

    fun getMaxTransaction(): T = datasource.getMaxTransaction()

    fun getAverageTransactionValue(): Double = datasource.getAverageTransactionValue()

    fun getFilteredTransactions(transactions: List<T>, filter: TransactionFilter): List<T> =
        datasource.getFilteredTransactions(transactions, filter)

}