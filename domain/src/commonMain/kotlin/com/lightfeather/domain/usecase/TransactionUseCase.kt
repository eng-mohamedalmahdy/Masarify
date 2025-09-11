package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.model.transaction.TransactionFilter
import com.lightfeather.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class CreateTransaction(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(transaction: Transaction): DomainResult<Int> =
        transactionRepository.createTransaction(transaction)
}

class UpdateTransaction(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(newTransaction: Transaction): DomainResult<Boolean> =
        transactionRepository.updateTransaction(newTransaction)
}

class DeleteTransaction(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(transaction: Transaction): DomainResult<Boolean> =
        transactionRepository.deleteTransaction(transaction)
}

class GetTransactionById<T : Transaction>(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(id: Int) = transactionRepository.getTransactionById<T>(id)
}

class GetAllTransactions(
    private val allTransactionsRepository: TransactionRepository,
) {
    suspend operator fun invoke(): DomainResult<Flow<List<Transaction>>> =
        allTransactionsRepository.getAllTransactions()
}

// Aggregates — all pushed into repo
class GetMaxTransaction(
    val repository: TransactionRepository,
) {
    suspend inline operator fun <reified T : Transaction> invoke() = repository.getMaxTransactionOfType(T::class)
}

class GetMinTransaction(
    val repository: TransactionRepository,
) {
    suspend inline operator fun <reified T : Transaction> invoke() = repository.getMinTransactionOfType(T::class)
}

class GetAverageTransactionValue(
    val repository: TransactionRepository,
) {
    suspend inline operator fun <reified T : Transaction> invoke() =
        repository.getAverageTransactionValueOfType(T::class)
}

class GetFilteredTransactions(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(filter: TransactionFilter) = repository.getFilteredTransactions(filter)
}

class GetTotalTransactionsByCategories<T : Transaction>(
    val repository: TransactionRepository,
) {
    suspend inline operator fun <reified T : Transaction> invoke() =
        repository.getTotalTransactionsOfTypesAndCategories(type = T::class)
}

class GetTotalExpenseOfCurrency(
    val repository: TransactionRepository,
) {
    suspend inline operator fun <reified T : Transaction> invoke(currency: Currency) =
        repository.getTotalTransactionsOfTypeAndCurrency<Transaction.Expense>(currency, Transaction.Expense::class)
}

class GetTotalIncomeOfCurrency(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(currency: Currency) =
        repository.getTotalTransactionsOfTypeAndCurrency(currency, Transaction.Income::class)
}
