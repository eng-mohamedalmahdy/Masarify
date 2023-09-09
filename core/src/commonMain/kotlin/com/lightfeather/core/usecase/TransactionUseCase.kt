package com.lightfeather.core.usecase

import com.lightfeather.core.data.repository.TransactionRepository
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter

class CreateTransaction<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke(transaction: T) = transactionRepository.createTransaction(transaction)
}

class UpdateTransaction<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke(transaction: T) = transactionRepository.updateTransaction(transaction)
}

class DeleteTransaction<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke(transaction: T) = transactionRepository.deleteTransaction(transaction)
}

class GetMaxTransaction<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke() = transactionRepository.getMaxTransaction()
}

class GetMinTransaction<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke() = transactionRepository.getMinTransaction()
}

class GetAverageTransactionValue<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke() = transactionRepository.getAverageTransactionValue()
}
class GetFilteredTransactions<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke(transactions: List<T>, filter: TransactionFilter) =
        transactionRepository.getFilteredTransactions(transactions, filter)
}

class GetAllTransactions<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke() = transactionRepository.getAllTransactions()
}

class GetTransactionById<T : Transaction>(private val transactionRepository: TransactionRepository<T>) {
    suspend operator fun invoke(id: Int) = transactionRepository.getTransactionById(id)
}







