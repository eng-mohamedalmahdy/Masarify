package data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.lightfeather.core.data.datasource.transactions.TransferDatasource
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Category
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.DomainResult
import com.lightfeather.core.domain.toDomainResult
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter
import com.lightfeather.masarify.database.BankAccountsQueries
import com.lightfeather.masarify.database.TransactionsQueries
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TransferDatasourceLocalImp(
    private val queries: TransactionsQueries,
    private val accountsQueries: BankAccountsQueries
) : TransferDatasource {


    override suspend fun createTransaction(transaction: Transaction.Transfer): DomainResult<Int> {
        with(transaction) {
            queries.insertTransfer(
                name,
                description,
                amount,
                timestamp,
                account.id.toLong(),
                receiverAccount.id.toLong(),
                fee
            )
            val id = queries.selectLastInsertedRowId().executeAsOne()
            return id.toInt().toDomainResult()
        }
    }


    override suspend fun deleteTransaction(transaction: Transaction): Boolean {
        queries.deleteTransaction(transaction.id.toLong())
        queries.deleteTransactionCategory(transaction.id.toLong())
        return true
    }

    override suspend fun getAllTransactions(): Flow<List<Transaction.Transfer>> {
        return queries.getAllTransactionsOfType("Transfer") { id, transactionName, transactionDescription, amount, timestamp, receiverAccountId, fee, accountId, accountName, accountDescription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Transfer(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription,
                amount = amount ?: 0.0,
                timestamp = timestamp ?: 0L,
                account = Account(
                    accountId.toInt(), accountName, Currency(currencyId.toInt(), currencyName, currencySign),
                    accountDescription, accountBalance, accountColor, accountLogo.toString()
                ),
                receiverAccount = accountsQueries.getAccountById(
                    receiverAccountId ?: 0
                ) { id: Long, name: String, description: String?, balance: Double, currency: Long, color: String, logo: String?, currencyId: Long, currencyName: String, sign: String ->
                    Account(
                        id.toInt(),
                        name,
                        Currency(currencyId.toInt(), currencyName, currencySign),
                        description,
                        balance,
                        color,
                        logo.toString()
                    )
                }.executeAsOne(),
                fee = fee ?: 0.0

            )
        }.asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun getTransactionById(id: Int): DomainResult<Transaction.Transfer> {
        return queries.getTransactionOfTypeById(
            "Transfer",
            id.toLong()
        ) { id: Long, transactionName: String?, transactionDescription: String?, amount: Double?, timestamp: Long?, receiverAccountId: Long?, fee: Double?, accountId: Long, accountName: String, accountDescription: String?, accountBalance: Double, accountColor: String, accountLogo: String?, currencyId: Long, currencyName: String, currencySign: String ->
            Transaction.Transfer(
                id.toInt(),
                transactionName.toString(),
                transactionDescription,
                amount ?: 0.0, timestamp ?: 0L,
                Account(
                    accountId.toInt(),
                    accountName,
                    Currency(currencyId.toInt(), currencyName, currencySign),
                    accountDescription,
                    accountBalance,
                    accountColor,
                    accountLogo.toString()
                ),
                receiverAccount = accountsQueries.getAccountById(
                    receiverAccountId ?: 0
                ) { id: Long, name: String, description: String?, balance: Double, currency: Long, color: String, logo: String?, currencyId: Long, currencyName: String, sign: String ->
                    Account(
                        id.toInt(),
                        name,
                        Currency(currencyId.toInt(), currencyName, currencySign),
                        description,
                        balance,
                        color,
                        logo.toString()
                    )
                }.executeAsOne(),
                fee ?: 0.0
            )
        }.executeAsOne().toDomainResult()
    }

    override suspend fun getMinTransaction(): Flow<Transaction.Transfer> {
        return queries.getMinExpenseOfTransactionType("Transfer") { id: Long, transactionName: String?, transactionDescription: String?, amount: Double?, timestamp: Long?, receiverAccountId: Long?, fee: Double?, accountId: Long, accountName: String, accountDescription: String?, accountBalance: Double, accountColor: String, accountLogo: String?, currencyId: Long, currencyName: String, currencySign: String ->
            Transaction.Transfer(
                id.toInt(),
                transactionName.toString(),
                transactionDescription,
                amount ?: 0.0, timestamp ?: 0L,
                Account(
                    accountId.toInt(),
                    accountName,
                    Currency(currencyId.toInt(), currencyName, currencySign),
                    accountDescription,
                    accountBalance,
                    accountColor,
                    accountLogo.toString()
                ),
                receiverAccount = accountsQueries.getAccountById(
                    receiverAccountId ?: 0
                ) { id: Long, name: String, description: String?, balance: Double, currency: Long, color: String, logo: String?, currencyId: Long, currencyName: String, sign: String ->
                    Account(
                        id.toInt(),
                        name,
                        Currency(currencyId.toInt(), currencyName, currencySign),
                        description,
                        balance,
                        color,
                        logo.toString()
                    )
                }.executeAsOne(),
                fee ?: 0.0
            )
        }.asFlow().mapToOne(Dispatchers.IO)
    }

    override suspend fun getMaxTransaction(): Flow<Transaction.Transfer> {
        return queries.getMaxExpenseOfTransactionType("Transfer") { id: Long, transactionName: String?, transactionDescription: String?, amount: Double?, timestamp: Long?, receiverAccountId: Long?, fee: Double?, accountId: Long, accountName: String, accountDescription: String?, accountBalance: Double, accountColor: String, accountLogo: String?, currencyId: Long, currencyName: String, currencySign: String ->
            Transaction.Transfer(
                id.toInt(),
                transactionName.toString(),
                transactionDescription,
                amount ?: 0.0, timestamp ?: 0L,
                Account(
                    accountId.toInt(),
                    accountName,
                    Currency(currencyId.toInt(), currencyName, currencySign),
                    accountDescription,
                    accountBalance,
                    accountColor,
                    accountLogo.toString()
                ),
                receiverAccount = accountsQueries.getAccountById(
                    receiverAccountId ?: 0
                ) { id: Long, name: String, description: String?, balance: Double, currency: Long, color: String, logo: String?, currencyId: Long, currencyName: String, sign: String ->
                    Account(
                        id.toInt(),
                        name,
                        Currency(currencyId.toInt(), currencyName, currencySign),
                        description,
                        balance,
                        color,
                        logo.toString()
                    )
                }.executeAsOne(),
                fee ?: 0.0
            )
        }.asFlow().mapToOne(Dispatchers.IO)

    }

    override suspend fun getAverageTransactionValue(): Flow<Double> {
        return queries.getAvgExpenseOfTransactionType("Transfer").asFlow().mapToOne(Dispatchers.IO)
            .map { it.averageAmount ?: 0.0 }
    }

    override suspend fun getFilteredTransactions(
        transactions: List<Transaction.Transfer>,
        filter: TransactionFilter
    ): Flow<List<Transaction.Transfer>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTransaction(transaction: Transaction.Transfer) {
        Napier.d(transaction.toString(), tag = "UPDATED3")
        queries.updateTransaction(
            name = transaction.name,
            description = transaction.description,
            amount = transaction.amount,
            timestamp = transaction.timestamp,
            account_id = transaction.account.id.toLong(), // Assuming account_id is a Long
            id = transaction.id.toLong() // Assuming id is a Long
        )
    }

    override suspend fun getTotalTransactionsOfCurrency(currency: Currency): Flow<Double> {
        return queries.getTotalTransactionsOfTypeOfCurrency("TRANSFER", currency.id.toLong()).asFlow()
            .mapToOne(Dispatchers.IO)
            .map { it.total_expenses ?: 0.0 }
    }

    override suspend fun getTotalTransactionsOfCategories(): Flow<List<Pair<Category, Double>>> {
        return queries.getTransactionsSumByCategoriesOfType("TRANSFER") { id, categoryName: String, categoryDescription: String, categoryColor: String, categoryIcon: String, totalExpenses: Double? ->
            Pair(
                Category(id.toInt(), categoryName, categoryDescription, categoryColor, categoryIcon),
                totalExpenses ?: 0.0
            )
        }.asFlow().mapToList(Dispatchers.IO)
    }

}