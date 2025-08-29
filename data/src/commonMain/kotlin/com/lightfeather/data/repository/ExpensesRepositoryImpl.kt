package com.lightfeather.data.repository

import com.lightfeather.data.database.drivers.SharedDatabase
import com.lightfeather.data.database.model.DbTransactionType
import com.lightfeather.domain.data.repository.ExpensesRepository
import com.lightfeather.domain.domain.Category
import com.lightfeather.domain.domain.Currency
import com.lightfeather.domain.domain.DomainResult
import com.lightfeather.domain.domain.runCatchingDomainResult
import com.lightfeather.domain.domain.runCatchingDomainResultSuspend
import com.lightfeather.domain.domain.transaction.Transaction
import com.lightfeather.domain.domain.transaction.TransactionFilter
import kotlinx.coroutines.flow.Flow

class ExpensesRepositoryImpl(
    private val database: SharedDatabase
) : ExpensesRepository {
    override suspend fun createTransaction(transaction: Transaction.Expense): DomainResult<Int> {
        return runCatchingDomainResultSuspend {
            database {
                val transactionsQueries = it.transactionsQueries
                val attachmentQueries = it.attachmentsQueries
                transactionsQueries.insertTransaction(
                    type = DbTransactionType.Expense.dbValue,
                    name = transaction.name,
                    description = transaction.description,
                    amount = transaction.amount,
                    timestamp = transaction.timestamp,
                    account_id = transaction.account.id.toLong(),
                    attachment = transaction.attachment.id
                ).toInt()
            }
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction): DomainResult<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllTransactions(): DomainResult<Flow<List<Transaction.Expense>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTransactionById(id: Int): DomainResult<Transaction.Expense> {
        TODO("Not yet implemented")
    }

    override suspend fun getMinTransaction(): DomainResult<Transaction.Expense> {
        TODO("Not yet implemented")
    }

    override suspend fun getMaxTransaction(): DomainResult<Transaction.Expense> {
        TODO("Not yet implemented")
    }

    override suspend fun getAverageTransactionValue(): DomainResult<Double> {
        TODO("Not yet implemented")
    }

    override suspend fun getFilteredTransactions(
        transactions: List<Transaction.Expense>,
        filter: TransactionFilter
    ): DomainResult<List<Transaction.Expense>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTransaction(newTransaction: Transaction.Expense): DomainResult<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalTransactionsOfCurrency(currency: Currency): DomainResult<Flow<Int>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalTransactionsOfCategories(): DomainResult<Flow<Map<Category, Int>>> {
        TODO("Not yet implemented")
    }

}