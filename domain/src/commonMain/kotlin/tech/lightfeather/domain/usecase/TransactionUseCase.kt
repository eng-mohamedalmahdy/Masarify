package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.PagedData
import tech.lightfeather.domain.model.sync.TransactionSyncPayload
import tech.lightfeather.domain.model.transaction.Transaction
import tech.lightfeather.domain.model.transaction.TransactionFilter
import tech.lightfeather.domain.repository.AccountRepository
import tech.lightfeather.domain.repository.CategoryRepository
import tech.lightfeather.domain.repository.TransactionRepository

class CreateTransaction(
    private val transactionRepository: TransactionRepository,
    private val syncHelper: SyncEnqueueHelper,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(transaction: Transaction): DomainResult<Int> =
        transactionRepository.createTransaction(transaction).also { result ->
            result.getOrNull()?.let { newId ->
                val txWithId =
                    when (transaction) {
                        is Transaction.Income -> transaction.copy(incomeId = newId)
                        is Transaction.Expense -> transaction.copy(expenseId = newId)
                        is Transaction.Transfer -> transaction.copy(transferId = newId)
                    }
                val payload = txWithId.toSyncPayload(accountRepository, categoryRepository)
                if (payload != null) {
                    syncHelper.enqueue("TRANSACTION", "CREATE", payload, TransactionSyncPayload.serializer(), newId)
                } else if (syncHelper.isLoggedIn()) {
                    syncHelper.fullSync()
                }
            }
        }
}

class UpdateTransaction(
    private val transactionRepository: TransactionRepository,
    private val syncHelper: SyncEnqueueHelper,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(newTransaction: Transaction): DomainResult<Boolean> =
        transactionRepository.updateTransaction(newTransaction).also { result ->
            if (result.isSuccess) {
                val payload = newTransaction.toSyncPayload(accountRepository, categoryRepository)
                if (payload != null) {
                    syncHelper.enqueue(
                        "TRANSACTION",
                        "UPDATE",
                        payload,
                        TransactionSyncPayload.serializer(),
                        newTransaction.id,
                    )
                } else if (syncHelper.isLoggedIn()) {
                    syncHelper.fullSync()
                }
            }
        }
}

class DeleteTransaction(
    private val transactionRepository: TransactionRepository,
    private val syncHelper: SyncEnqueueHelper,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(transactionId: Long): DomainResult<Boolean> {
        val snapshot =
            if (syncHelper.isLoggedIn()) {
                transactionRepository.getTransactionById<Transaction>(transactionId.toInt()).getOrNull()
            } else {
                null
            }
        return transactionRepository.deleteTransaction(transactionId).also { result ->
            if (result.isSuccess && snapshot != null) {
                val payload = snapshot.toSyncPayload(accountRepository, categoryRepository)
                if (payload != null) {
                    syncHelper.enqueue(
                        "TRANSACTION",
                        "DELETE",
                        payload,
                        TransactionSyncPayload.serializer(),
                        transactionId.toInt(),
                    )
                } else if (syncHelper.isLoggedIn()) {
                    syncHelper.fullSync()
                }
            }
        }
    }
}

internal suspend fun Transaction.toSyncPayload(
    accountRepository: AccountRepository,
    categoryRepository: CategoryRepository,
): TransactionSyncPayload? {
    val accountId = accountRepository.getRemoteIdByLocalId(account.id).getOrNull() ?: return null
    return when (this) {
        is Transaction.Income ->
            TransactionSyncPayload(
                type = "INCOME",
                accountId = accountId,
                name = name,
                description = description,
                amount = amount,
                timestamp = timestamp,
                categoryId = categoryRepository.getRemoteIdByLocalId(source.id).getOrNull(),
            )

        is Transaction.Expense ->
            TransactionSyncPayload(
                type = "EXPENSE",
                accountId = accountId,
                name = name,
                description = description,
                amount = amount,
                timestamp = timestamp,
                categoryIds =
                    categories
                        .mapNotNull { categoryRepository.getRemoteIdByLocalId(it.id).getOrNull() }
                        .ifEmpty { null },
            )

        is Transaction.Transfer ->
            TransactionSyncPayload(
                type = "TRANSFER",
                accountId = accountId,
                name = name,
                description = description,
                amount = amount,
                timestamp = timestamp,
                toAccountId = accountRepository.getRemoteIdByLocalId(receiverAccount.id).getOrNull(),
                fee = fee,
            )
    }
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

// Pagination use cases
class GetAllTransactionsPaged(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(page: Int): DomainResult<Flow<PagedData<Transaction>>> =
        repository.getAllTransactionsPaged(page)
}

class GetAllTransactionsOfTypePaged<T : Transaction>(
    val repository: TransactionRepository,
) {
    suspend inline operator fun <reified T : Transaction> invoke(page: Int): DomainResult<Flow<PagedData<T>>> =
        repository.getAllTransactionsOfTypePaged(T::class, page)
}

class GetFilteredTransactionsPaged(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(
        filter: TransactionFilter,
        page: Int,
    ): DomainResult<Flow<PagedData<Transaction>>> = repository.getFilteredTransactionsPaged(filter, page)
}

class GetTransactionCount(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(): DomainResult<Long> = repository.getTransactionCount()
}

class GetTransactionCountOfType<T : Transaction>(
    val repository: TransactionRepository,
) {
    suspend inline operator fun <reified T : Transaction> invoke(): DomainResult<Long> =
        repository.getTransactionCountOfType(T::class)
}

class GetFilteredTransactionCount(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(filter: TransactionFilter): DomainResult<Long> =
        repository.getFilteredTransactionCount(filter)
}
