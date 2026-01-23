package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.data.local.database.model.toDbTransactionType
import com.lightfeather.data.mapper.toDomainTransactions
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.PagedData
import com.lightfeather.domain.model.runCatchingDomainResultSuspend
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.model.transaction.TransactionFilter
import com.lightfeather.domain.repository.AttachmentRepository
import com.lightfeather.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

class TransactionsRepositoryImpl(
    private val sharedDatabase: SharedDatabase,
    private val attachmentRepository: AttachmentRepository,
) : TransactionRepository {
    companion object {
        const val PAGE_SIZE = 20
    }

    override suspend fun createTransaction(transaction: Transaction): DomainResult<Int> =
        runCatchingDomainResultSuspend {
            val transactionCategories =
                when (transaction) {
                    is Transaction.Expense -> transaction.categories
                    is Transaction.Income -> listOf(transaction.source)
                    is Transaction.Transfer -> listOf(Category.Transfer)
                }
            val transactionId =
                sharedDatabase {
                    val transactionsQueries = it.transactionsQueries
                    val bankAccountsQueries = it.bankAccountsQueries

                    transactionsQueries.transactionWithResult {
                        // Insert transaction
                        transactionsQueries.insertTransaction(
                            type = transaction.toDbTransactionType().dbValue,
                            name = transaction.name,
                            description = transaction.description,
                            amount = transaction.amount,
                            timestamp = transaction.timestamp,
                            account_id = transaction.account.id.toLong(),
                        )
                        val id = transactionsQueries.selectLastInsertedRowId().awaitAsOne()

                        // Insert transaction categories
                        transactionCategories.forEach { category ->
                            transactionsQueries.insertTransactionCategory(
                                transaction_id = id,
                                category_id = category.id.toLong(),
                            )
                        }

                        // Update account balance(s) based on transaction type
                        when (transaction) {
                            is Transaction.Income -> {
                                // Increase account balance by amount
                                val newBalance = transaction.accountNewBalance
                                bankAccountsQueries.updateAccountBalance(
                                    balance = newBalance,
                                    id = transaction.account.id.toLong(),
                                )
                            }
                            is Transaction.Expense -> {
                                // Decrease account balance by amount
                                val newBalance = transaction.accountNewBalance
                                bankAccountsQueries.updateAccountBalance(
                                    balance = newBalance,
                                    id = transaction.account.id.toLong(),
                                )
                            }
                            is Transaction.Transfer -> {
                                // Decrease source account by amount + fee
                                val sourceNewBalance = transaction.accountNewBalance
                                bankAccountsQueries.updateAccountBalance(
                                    balance = sourceNewBalance,
                                    id = transaction.account.id.toLong(),
                                )
                                // Increase receiver account by amount
                                val receiverNewBalance = transaction.receiverAccountNewBalance
                                bankAccountsQueries.updateAccountBalance(
                                    balance = receiverNewBalance,
                                    id = transaction.receiverAccount.id.toLong(),
                                )
                            }
                        }

                        // Insert attachments
                        transaction.attachments.forEach { attachment ->
                            val attachmentWithId =
                                attachment.copy(
                                    entityType = com.lightfeather.domain.model.AttachmentEntityType.TRANSACTION,
                                    entityId = id.toInt(),
                                )
                            attachmentRepository.createAttachment(attachmentWithId)
                        }

                        id.toInt()
                    }
                }

            transactionId
        }

    override suspend fun deleteTransaction(transactionId: Long): DomainResult<Boolean> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                val transactionsQueries = it.transactionsQueries
                val bankAccountsQueries = it.bankAccountsQueries

                transactionsQueries.transactionWithResult {
                    // Get transaction to reverse its effect
                    val transactionRows = transactionsQueries.getTransactionById(transactionId).awaitAsList()
                    val transaction =
                        transactionRows.toDomainTransactions().firstOrNull()
                            ?: error("Transaction not found: $transactionId")

                    // Reverse transaction's effect on account balance(s)
                    when (transaction) {
                        is Transaction.Income -> {
                            // Reverse income: decrease account balance
                            val reversedBalance = transaction.accountOldBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = reversedBalance,
                                id = transaction.account.id.toLong(),
                            )
                        }
                        is Transaction.Expense -> {
                            // Reverse expense: increase account balance
                            val reversedBalance = transaction.accountOldBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = reversedBalance,
                                id = transaction.account.id.toLong(),
                            )
                        }
                        is Transaction.Transfer -> {
                            // Reverse transfer: increase source account, decrease receiver account
                            val sourceReversedBalance = transaction.accountOldBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = sourceReversedBalance,
                                id = transaction.account.id.toLong(),
                            )
                            val receiverReversedBalance = transaction.receiverAccountOldBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = receiverReversedBalance,
                                id = transaction.receiverAccount.id.toLong(),
                            )
                        }
                    }

                    // Delete attachments
                    when (val result = attachmentRepository.getAttachmentsByTransactionId(transaction.id)) {
                        is DomainResult.Success -> {
                            result.data.forEach { attachment ->
                                attachmentRepository.deleteAttachment(attachment.id)
                            }
                        }
                        is DomainResult.Failure -> {
                            // No attachments to delete or error fetching them
                        }
                    }

                    // Delete transaction
                    transactionsQueries.deleteTransaction(transactionId)

                    true
                }
            }
        }

    override suspend fun <T : Transaction> getAllTransactionsOfType(type: KClass<T>): DomainResult<Flow<List<T>>> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries
                    .getAllTransactionsOfType(type.toDbTransactionType().dbValue)
                    .asFlow()
                    .map { query ->
                        query.awaitAsList().toDomainTransactions().map { it as T }
                    }
            }
        }

    override suspend fun <T : Transaction> getTransactionById(id: Int): DomainResult<T> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                val rows = it.transactionsQueries.getTransactionById(id.toLong()).awaitAsList()
                rows.toDomainTransactions().first() as T
            }
        }

    override suspend fun <T : Transaction> getMinTransactionOfType(type: KClass<T>): DomainResult<T> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                val rows =
                    it.transactionsQueries
                        .getMinTransactionOfType(type.toDbTransactionType().dbValue)
                        .awaitAsList()
                rows.toDomainTransactions().first() as T
            }
        }

    override suspend fun <T : Transaction> getMaxTransactionOfType(type: KClass<T>): DomainResult<T> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                val rows =
                    it.transactionsQueries
                        .getMaxTransactionOfType(type.toDbTransactionType().dbValue)
                        .awaitAsList()
                rows.toDomainTransactions().first() as T
            }
        }

    override suspend fun <T : Transaction> getAverageTransactionValueOfType(type: KClass<T>): DomainResult<Double> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries
                    .getAverageTransactionValueOfType(type.toDbTransactionType().dbValue)
                    .awaitAsOne()
                    .averageAmount ?: 0.0
            }
        }

    override suspend fun getFilteredTransactions(filter: TransactionFilter): DomainResult<List<Transaction>> =
        runCatchingDomainResultSuspend {
            if (filter.isEmpty()) {
                // If filter is empty, return all transactions
                sharedDatabase {
                    it.transactionsQueries
                        .getAllTransactions()
                        .awaitAsList()
                        .toDomainTransactions()
                }
            } else {
                // For comprehensive filtering, we currently filter in memory
                // In production, this should be optimized with SQL WHERE clauses
                sharedDatabase { database ->
                    val allTransactions =
                        database.transactionsQueries
                            .getAllTransactions()
                            .awaitAsList()
                            .toDomainTransactions()

                    allTransactions.filter { filter.matches(it) }
                }
            }
        }

    override suspend fun updateTransaction(newTransaction: Transaction): DomainResult<Boolean> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                val transactionsQueries = it.transactionsQueries
                val bankAccountsQueries = it.bankAccountsQueries

                transactionsQueries.transactionWithResult {
                    // Get old transaction to reverse its effect
                    val oldTransactionRows =
                        transactionsQueries
                            .getTransactionById(newTransaction.id.toLong())
                            .awaitAsList()
                    val oldTransaction =
                        oldTransactionRows.toDomainTransactions().firstOrNull()
                            ?: error("Transaction not found: ${newTransaction.id}")

                    // Reverse old transaction's effect on account balance(s)
                    when (oldTransaction) {
                        is Transaction.Income -> {
                            // Reverse: decrease account balance by old amount
                            val reversedBalance = oldTransaction.accountOldBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = reversedBalance,
                                id = oldTransaction.account.id.toLong(),
                            )
                        }
                        is Transaction.Expense -> {
                            // Reverse: increase account balance by old amount
                            val reversedBalance = oldTransaction.accountOldBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = reversedBalance,
                                id = oldTransaction.account.id.toLong(),
                            )
                        }
                        is Transaction.Transfer -> {
                            // Reverse: increase source account, decrease receiver account
                            val sourceReversedBalance = oldTransaction.accountOldBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = sourceReversedBalance,
                                id = oldTransaction.account.id.toLong(),
                            )
                            val receiverReversedBalance = oldTransaction.receiverAccountOldBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = receiverReversedBalance,
                                id = oldTransaction.receiverAccount.id.toLong(),
                            )
                        }
                    }

                    // Update transaction
                    transactionsQueries.updateTransaction(
                        id = newTransaction.id.toLong(),
                        type = newTransaction.toDbTransactionType().dbValue,
                        name = newTransaction.name,
                        description = newTransaction.description,
                        amount = newTransaction.amount,
                        timestamp = newTransaction.timestamp,
                        account_id = newTransaction.account.id.toLong(),
                    )

                    // Apply new transaction's effect on account balance(s)
                    when (newTransaction) {
                        is Transaction.Income -> {
                            // Increase account balance by new amount
                            val newBalance = newTransaction.accountNewBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = newBalance,
                                id = newTransaction.account.id.toLong(),
                            )
                        }
                        is Transaction.Expense -> {
                            // Decrease account balance by new amount
                            val newBalance = newTransaction.accountNewBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = newBalance,
                                id = newTransaction.account.id.toLong(),
                            )
                        }
                        is Transaction.Transfer -> {
                            // Decrease source account by amount + fee
                            val sourceNewBalance = newTransaction.accountNewBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = sourceNewBalance,
                                id = newTransaction.account.id.toLong(),
                            )
                            // Increase receiver account by amount
                            val receiverNewBalance = newTransaction.receiverAccountNewBalance
                            bankAccountsQueries.updateAccountBalance(
                                balance = receiverNewBalance,
                                id = newTransaction.receiverAccount.id.toLong(),
                            )
                        }
                    }

                    // Get existing attachments
                    val existingAttachments =
                        when (val result = attachmentRepository.getAttachmentsByTransactionId(newTransaction.id)) {
                            is DomainResult.Success -> result.data
                            is DomainResult.Failure -> emptyList()
                        }

                    // Delete attachments that are no longer in the transaction
                    val newAttachmentIds = newTransaction.attachments.map { it.id }.toSet()
                    existingAttachments
                        .filter { it.id !in newAttachmentIds }
                        .forEach { attachmentRepository.deleteAttachment(it.id) }

                    // Add new attachments (those with id = -1)
                    newTransaction.attachments
                        .filter { it.id < 0 }
                        .forEach { attachment ->
                            val attachmentWithId =
                                attachment.copy(
                                    entityType = com.lightfeather.domain.model.AttachmentEntityType.TRANSACTION,
                                    entityId = newTransaction.id,
                                )
                            attachmentRepository.createAttachment(attachmentWithId)
                        }

                    true
                }
            }
        }

    override suspend fun <T : Transaction> getTotalTransactionsOfTypeAndCurrency(
        currency: Currency,
        type: KClass<T>,
    ): DomainResult<Flow<Double>> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries
                    .getTotalTransactionsOfTypeOfCurrency(
                        type = type.toDbTransactionType().dbValue,
                        currencyId = currency.id.toLong(),
                    ).asFlow()
                    .map { it.awaitAsOneOrNull()?.total_amount ?: 0.0 }
            }
        }

    override suspend fun <T : Transaction> getTotalTransactionsOfTypesAndCategories(
        type: KClass<T>,
    ): DomainResult<Flow<Map<Category, Double>>> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries
                    .getTransactionsSumByCategoriesOfType(
                        type.toDbTransactionType().dbValue,
                    ).asFlow()
                    .map {
                        it.awaitAsList().associate {
                            Pair(
                                Category(
                                    it.category_id.toInt(),
                                    it.category_name,
                                    it.category_description,
                                    it.category_color,
                                    it.category_icon,
                                ),
                                it.total_amount ?: 0.0,
                            )
                        }
                    }
            }
        }

    override suspend fun getAllTransactions(): DomainResult<Flow<List<Transaction>>> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries.getAllTransactions().asFlow().map {
                    it.awaitAsList().toDomainTransactions()
                }
            }
        }

    // Pagination methods
    override suspend fun getAllTransactionsPaged(page: Int): DomainResult<Flow<PagedData<Transaction>>> =
        runCatchingDomainResultSuspend {
            sharedDatabase { database ->
                val offset = page * PAGE_SIZE
                database.transactionsQueries
                    .getAllTransactionsPaged(limit = PAGE_SIZE.toLong(), offset = offset.toLong())
                    .asFlow()
                    .map { query ->
                        val transactions = query.awaitAsList().toDomainTransactions()
                        val totalCount =
                            database.transactionsQueries
                                .getTransactionCount()
                                .awaitAsOne()

                        PagedData.create(
                            data = transactions,
                            page = page,
                            pageSize = PAGE_SIZE,
                            totalItems = totalCount,
                        )
                    }
            }
        }

    override suspend fun <T : Transaction> getAllTransactionsOfTypePaged(
        type: KClass<T>,
        page: Int,
    ): DomainResult<Flow<PagedData<T>>> =
        runCatchingDomainResultSuspend {
            sharedDatabase { database ->
                val offset = page * PAGE_SIZE
                database.transactionsQueries
                    .getAllTransactionsOfTypePaged(
                        type = type.toDbTransactionType().dbValue,
                        limit = PAGE_SIZE.toLong(),
                        offset = offset.toLong(),
                    ).asFlow()
                    .map { query ->
                        val transactions =
                            query
                                .awaitAsList()
                                .toDomainTransactions()
                                .map { it as T }

                        val totalCount =
                            database.transactionsQueries
                                .getTransactionCountOfType(type.toDbTransactionType().dbValue)
                                .awaitAsOne()

                        PagedData.create(
                            data = transactions,
                            page = page,
                            pageSize = PAGE_SIZE,
                            totalItems = totalCount,
                        )
                    }
            }
        }

    override suspend fun getFilteredTransactionsPaged(
        filter: TransactionFilter,
        page: Int,
    ): DomainResult<Flow<PagedData<Transaction>>> {
        // For now, this will filter in memory since filtering is done via TransactionFilter logic
        // In a real implementation, we would convert filter to SQL queries for better performance
        return runCatchingDomainResultSuspend {
            sharedDatabase { database ->
                database.transactionsQueries
                    .getAllTransactions()
                    .asFlow()
                    .map { query ->
                        // Get all transactions and filter in memory
                        val allTransactions =
                            query
                                .awaitAsList()
                                .toDomainTransactions()
                                .filter { filter.matches(it) }

                        val totalCount = allTransactions.size.toLong()
                        val offset = page * PAGE_SIZE
                        val pagedTransactions =
                            allTransactions
                                .drop(offset)
                                .take(PAGE_SIZE)

                        PagedData.create(
                            data = pagedTransactions,
                            page = page,
                            pageSize = PAGE_SIZE,
                            totalItems = totalCount,
                        )
                    }
            }
        }
    }

    override suspend fun getTransactionCount(): DomainResult<Long> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries.getTransactionCount().awaitAsOne()
            }
        }

    override suspend fun <T : Transaction> getTransactionCountOfType(type: KClass<T>): DomainResult<Long> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries
                    .getTransactionCountOfType(type.toDbTransactionType().dbValue)
                    .awaitAsOne()
            }
        }

    override suspend fun getFilteredTransactionCount(filter: TransactionFilter): DomainResult<Long> =
        runCatchingDomainResultSuspend {
            sharedDatabase { database ->
                // For now, filter in memory since filtering is done via TransactionFilter logic
                val allTransactions =
                    database.transactionsQueries
                        .getAllTransactions()
                        .awaitAsList()
                        .toDomainTransactions()
                        .count { filter.matches(it) }
                        .toLong()

                allTransactions
            }
        }
}
