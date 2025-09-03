package com.lightfeather.data.repository

import app.cash.sqldelight.coroutines.asFlow
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.data.local.database.model.DbTransactionType
import com.lightfeather.data.local.database.model.toDbTransactionType
import com.lightfeather.domain.repository.TransactionRepository
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.Attachment
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.runCatchingDomainResultSuspend
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.model.transaction.TransactionFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.V_transactions
import kotlin.reflect.KClass

class TransactionsRepositoryImpl(
    private val sharedDatabase: SharedDatabase
) : TransactionRepository {
    override suspend fun createTransaction(transaction: Transaction): DomainResult<Int> {
        return runCatchingDomainResultSuspend {
            sharedDatabase {
                val transactionsQueries = it.transactionsQueries
                transactionsQueries.insertTransaction(
                    type = transaction.toDbTransactionType().dbValue,
                    name = transaction.name,
                    description = transaction.description,
                    amount = transaction.amount,
                    timestamp = transaction.timestamp,
                    account_id = transaction.account.id.toLong()
                ).toInt()
            }
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction): DomainResult<Boolean> {
        return runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries.deleteTransaction(transaction.id.toLong())
            }
            true
        }
    }

    override suspend fun <T : Transaction> getAllTransactionsOfType(
        type: KClass<T>
    ): DomainResult<Flow<List<T>>> {
        return runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries.getAllTransactionsOfType(type.toDbTransactionType().dbValue)
                    .asFlow()
                    .map { query ->
                        query.executeAsList().toDomainTransactions().map { it as T }
                    }
            }
        }
    }


    override suspend fun <T : Transaction> getTransactionById(id: Int): DomainResult<T> {
        return runCatchingDomainResultSuspend {
            sharedDatabase {
                val rows = it.transactionsQueries.getTransactionById(id.toLong()).executeAsList()
                rows.toDomainTransactions().first() as T
            }
        }
    }

    override suspend fun <T : Transaction> getMinTransactionOfType(type: KClass<T>): DomainResult<T> {
        return runCatchingDomainResultSuspend {
            sharedDatabase {
                val rows = it.transactionsQueries.getMinTransactionOfType(type.toDbTransactionType().dbValue)
                    .executeAsList()
                rows.toDomainTransactions().first() as T
            }
        }
    }

    override suspend fun <T : Transaction> getMaxTransactionOfType(type: KClass<T>): DomainResult<T> {
        return runCatchingDomainResultSuspend {
            sharedDatabase {
                val rows = it.transactionsQueries.getMaxTransactionOfType(type.toDbTransactionType().dbValue)
                    .executeAsList()
                rows.toDomainTransactions().first() as T
            }
        }
    }

    override suspend fun <T : Transaction> getAverageTransactionValueOfType(
        type: KClass<T>
    ): DomainResult<Double> {
        return runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries.getAverageTransactionValueOfType(type.toDbTransactionType().dbValue)
                    .executeAsOne()
                    .averageAmount ?: 0.0
            }
        }
    }

    override suspend fun getFilteredTransactions(filter: TransactionFilter): DomainResult<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTransaction(newTransaction: Transaction): DomainResult<Boolean> {
        return runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries.updateTransaction(
                    id = newTransaction.id.toLong(),
                    type = newTransaction.toDbTransactionType().dbValue,
                    name = newTransaction.name,
                    description = newTransaction.description,
                    amount = newTransaction.amount,
                    timestamp = newTransaction.timestamp,
                    account_id = newTransaction.account.id.toLong()
                )
                true
            }
        }
    }

    override suspend fun <T : Transaction> getTotalTransactionsOfTypeAndCurrency(
        currency: Currency,
        type: KClass<T>
    ): DomainResult<Flow<Double>> {
        return runCatchingDomainResultSuspend {

            sharedDatabase {
                it.transactionsQueries.getTotalTransactionsOfTypeOfCurrency(
                    type = type.toDbTransactionType().dbValue,
                    currencyId = currency.id.toLong()
                ).asFlow().map { it.executeAsOneOrNull()?.total_amount ?: 0.0 }
            }
        }
    }


    override suspend fun <T : Transaction> getTotalTransactionsOfTypesAndCategories(type: KClass<T>): DomainResult<Flow<Map<Category, Double>>> {
        return runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries.getTransactionsSumByCategoriesOfType(
                    type.toDbTransactionType().dbValue
                ).asFlow().map {
                    it.executeAsList().associate {
                        Pair(
                            Category(
                                it.category_id.toInt(),
                                it.category_name,
                                it.category_description,
                                it.category_color,
                                it.category_icon
                            ), it.total_amount ?: 0.0
                        )
                    }
                }
            }
        }
    }

    override suspend fun getAllTransactions(): DomainResult<Flow<List<Transaction>>> {
        return runCatchingDomainResultSuspend {
            sharedDatabase {
                it.transactionsQueries.getAllTransactions().asFlow().map {
                    it.executeAsList().toDomainTransactions()
                }
            }
        }
    }

    fun List<V_transactions>.toDomainTransactions(): List<Transaction> {
        return this
            .groupBy { it.transactionId }
            .map { (_, rows) ->
                val first = rows.first()

                val account = Account(
                    id = first.accountId.toInt(),
                    name = first.accountName,
                    description = first.accountDescription,
                    balance = first.accountBalance,
                    color = first.accountColor,
                    logo = first.accountLogo.orEmpty(),
                    currency = Currency(
                        name = first.currencyName,
                        sign = first.currencySign,
                        id = first.currencyId.toInt()
                    )
                )

                val receiverAccount = Account(
                    id = (first.receiverAccountId ?: -1).toInt(),
                    name = first.receiverAccountName.orEmpty(),
                    description = first.receiverAccountDescription.orEmpty(),
                    balance = first.receiverAccountBalance ?: 0.0,
                    color = first.receiverAccountColor.orEmpty(),
                    logo = first.receiverAccountLogo.orEmpty(),
                    currency = Currency(
                        name = first.receiverCurrencyName.orEmpty(),
                        sign = first.receiverCurrencySign.orEmpty(),
                        id = first.receiverCurrencyId?.toInt() ?: -1,
                    )
                )

                val attachments = rows
                    .filter { it.attachmentId != null }
                    .map {
                        Attachment(
                            id = it.attachmentId!!.toInt(),
                            fileName = it.attachmentName.orEmpty(),
                            mimeType = it.attachmentMimeType.orEmpty(),
                            fileContent = it.attachmentData ?: byteArrayOf(),
                            transactionId = first.transactionId.toInt()
                        )
                    }
                    .distinctBy { it.id }

                val categories = rows
                    .filter { it.categoryId != null }
                    .map {
                        Category(
                            id = it.categoryId?.toInt()!!,
                            name = it.categoryName!!,
                            description = it.categoryDescription!!,
                            color = it.categoryColor!!,
                            icon = it.categoryIcon!!
                        )
                    }
                    .distinctBy { it.id }

                when (first.transactionType?.toDbTransactionType()) {
                    DbTransactionType.Income -> Transaction.Income(
                        id = first.transactionId.toInt(),
                        name = first.transactionName.orEmpty(),
                        description = first.transactionDescription,
                        amount = first.transactionAmount ?: 0.0,
                        timestamp = first.transactionTimestamp ?: 0,
                        account = account,
                        source = categories.firstOrNull() ?: Category(-1, "Unknown", "", "#000000", "❓"),
                        attachments = attachments
                    )

                    DbTransactionType.Expense -> Transaction.Expense(
                        id = first.transactionId.toInt(),
                        name = first.transactionName.orEmpty(),
                        description = first.transactionDescription,
                        amount = first.transactionAmount ?: 0.0,
                        timestamp = first.transactionTimestamp ?: 0,
                        account = account,
                        categories = categories,
                        attachments = attachments
                    )

                    DbTransactionType.Transfer -> Transaction.Transfer(
                        id = first.transactionId.toInt(),
                        name = first.transactionName.orEmpty(),
                        description = first.transactionDescription,
                        amount = first.transactionAmount ?: 0.0,
                        timestamp = first.transactionTimestamp ?: 0,
                        account = account,
                        receiverAccount = receiverAccount,
                        fee = first.transactionFee ?: 0.0,
                        attachments = attachments
                    )

                    else -> throw IllegalArgumentException("Unknown transaction type: ${first.transactionType}")
                }
            }
    }


}
