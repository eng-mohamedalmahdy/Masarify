package tech.lightfeather.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.lightfeather.domain.model.Category
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.PagedData
import tech.lightfeather.domain.model.transaction.Transaction
import tech.lightfeather.domain.model.transaction.TransactionFilter
import kotlin.reflect.KClass

interface TransactionRepository {
    suspend fun createTransaction(transaction: Transaction): DomainResult<Int>

    suspend fun deleteTransaction(transactionId: Long): DomainResult<Boolean>

    suspend fun <T : Transaction> getAllTransactionsOfType(type: KClass<T>): DomainResult<Flow<List<T>>>

    suspend fun <T : Transaction> getTransactionById(id: Int): DomainResult<T>

    suspend fun <T : Transaction> getMinTransactionOfType(type: KClass<T>): DomainResult<T>

    suspend fun <T : Transaction> getMaxTransactionOfType(type: KClass<T>): DomainResult<T>

    suspend fun <T : Transaction> getAverageTransactionValueOfType(type: KClass<T>): DomainResult<Double>

    suspend fun getFilteredTransactions(filter: TransactionFilter): DomainResult<List<Transaction>>

    suspend fun updateTransaction(newTransaction: Transaction): DomainResult<Boolean>

    suspend fun <T : Transaction> getTotalTransactionsOfTypeAndCurrency(
        currency: Currency,
        type: KClass<T>,
    ): DomainResult<Flow<Double>>

    suspend fun <T : Transaction> getTotalTransactionsOfTypesAndCategories(
        type: KClass<T>,
    ): DomainResult<Flow<Map<Category, Double>>>

    suspend fun getAllTransactions(): DomainResult<Flow<List<Transaction>>>

    // Pagination methods
    suspend fun getAllTransactionsPaged(page: Int): DomainResult<Flow<PagedData<Transaction>>>

    suspend fun <T : Transaction> getAllTransactionsOfTypePaged(
        type: KClass<T>,
        page: Int,
    ): DomainResult<Flow<PagedData<T>>>

    suspend fun getFilteredTransactionsPaged(
        filter: TransactionFilter,
        page: Int,
    ): DomainResult<Flow<PagedData<Transaction>>>

    suspend fun getTransactionCount(): DomainResult<Long>

    suspend fun <T : Transaction> getTransactionCountOfType(type: KClass<T>): DomainResult<Long>

    suspend fun getFilteredTransactionCount(filter: TransactionFilter): DomainResult<Long>

    suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit>

    suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?>

    suspend fun getUnsyncedIds(): DomainResult<List<Int>>
}
