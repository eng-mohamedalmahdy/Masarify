package data.datasource.transactions

import domain.transaction.Transaction
import domain.transaction.TransactionFilter

sealed interface TransactionDatasource<T : Transaction> {

    fun createTransaction(transaction: T)

    fun updateTransaction(transaction: T)
    abstract fun deleteTransaction(transaction: T): Boolean

    fun getAllTransactions(): List<T>

    fun getTransactionById(id: Int): T

    fun getMinTransaction(): T

    fun getMaxTransaction(): T

    fun getAverageTransactionValue(): Double

    fun getFilteredTransactions(transactions: List<T>, filter: TransactionFilter): List<T>

}