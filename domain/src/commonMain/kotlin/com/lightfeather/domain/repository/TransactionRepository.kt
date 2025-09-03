package com.lightfeather.domain.repository

import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.model.transaction.TransactionFilter
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass


interface TransactionRepository {

    suspend fun createTransaction(transaction: Transaction): DomainResult<Int>


    suspend fun deleteTransaction(transaction: Transaction): DomainResult<Boolean>

    suspend fun <T : Transaction> getAllTransactionsOfType(type: KClass<T>): DomainResult<Flow<List<T>>>

    suspend fun <T : Transaction> getTransactionById(id: Int): DomainResult<T>

    suspend fun <T : Transaction> getMinTransactionOfType(type: KClass<T>): DomainResult<T>

    suspend fun <T : Transaction> getMaxTransactionOfType(type: KClass<T>): DomainResult<T>

    suspend fun <T : Transaction> getAverageTransactionValueOfType(type: KClass<T>): DomainResult<Double>

    suspend fun getFilteredTransactions(filter: TransactionFilter): DomainResult<List<Transaction>>

    suspend fun updateTransaction(newTransaction: Transaction): DomainResult<Boolean>

    suspend fun <T : Transaction> getTotalTransactionsOfTypeAndCurrency(
        currency: Currency,
        type: KClass<T>
    ): DomainResult<Flow<Double>>

    suspend fun <T : Transaction> getTotalTransactionsOfTypesAndCategories(type: KClass<T>): DomainResult<Flow<Map<Category, Double>>>

    suspend fun getAllTransactions(): DomainResult<Flow<List<Transaction>>>
}