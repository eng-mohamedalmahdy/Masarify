package data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.lightfeather.core.data.datasource.transactions.IncomeDatasource
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Category
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.DomainResult
import com.lightfeather.core.domain.toDomainResult
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter
import com.lightfeather.masarify.database.TransactionsQueries
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IncomeDatasourceLocalImp(private val transactionsQueries: TransactionsQueries) : IncomeDatasource {
    override suspend fun createTransaction(transaction: Transaction.Income): DomainResult<Int> {
        with(transaction) {
            transactionsQueries.insertTransaction(
                "INCOME", name, description, amount, timestamp, account.id.toLong()
            )
            val id = transactionsQueries.selectLastInsertedRowId().executeAsOne()
            transactionsQueries.insertTransactionCategory(id, source.id.toLong())
            return id.toInt().toDomainResult()
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction): Boolean {
        transactionsQueries.deleteTransaction(transaction.id.toLong())
        transactionsQueries.deleteTransactionCategory(transaction.id.toLong())
        return true
    }

    override suspend fun getAllTransactions(): Flow<List<Transaction.Income>> {
        return transactionsQueries.getAllTransactionsOfType("INCOME") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDescription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Income(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                source = transactionsQueries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, description: String, color: String, icon: String ->
                    Category(id.toInt(), name, description, color, icon)
                }.executeAsList().first(),
                amount = amount ?: 0.0,
                timestamp = timestamp ?: 0L,
                account = Account(
                    id = accountId.toInt(),
                    name = accountName,
                    currency = Currency(id = currencyId.toInt(), name = accountName, sign = currencySign),
                    description = accountDescription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun getTransactionById(id: Int): DomainResult<Transaction.Income> {
        return transactionsQueries.getTransactionOfTypeById(
            "INCOME",
            id.toLong()
        ) { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDescription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Income(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                source = transactionsQueries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, desceription: String, color: String, icon: String ->
                    Category(id.toInt(), name, desceription, color, icon)
                }.executeAsList().first(),
                amount = amount ?: 0.0,
                timestamp = timestamp ?: 0L,
                account = Account(
                    id = accountId.toInt(),
                    name = accountName,
                    currency = Currency(id = currencyId.toInt(), name = accountName, sign = currencySign),
                    description = accountDescription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.executeAsOne().toDomainResult()
    }

    override suspend fun getMinTransaction(): Flow<Transaction.Income> {
        return transactionsQueries.getMinExpenseOfTransactionType("INCOME") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDesceription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Income(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                source = transactionsQueries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, desceription: String, color: String, icon: String ->
                    Category(id.toInt(), name, desceription, color, icon)
                }.executeAsList().first(),
                amount = amount ?: 0.0,
                timestamp = timestamp ?: 0L,
                account = Account(
                    id = accountId.toInt(),
                    name = accountName,
                    currency = Currency(id = currencyId.toInt(), name = accountName, sign = currencySign),
                    description = accountDesceription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.asFlow().mapToOne(Dispatchers.IO)
    }

    override suspend fun getMaxTransaction(): Flow<Transaction.Income> {
        return transactionsQueries.getMaxExpenseOfTransactionType("INCOME") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDesceription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Income(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                source = transactionsQueries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, description: String, color: String, icon: String ->
                    Category(id.toInt(), name, description, color, icon)
                }.executeAsList().first(),
                amount = amount ?: 0.0,
                timestamp = timestamp ?: 0L,
                account = Account(
                    id = accountId.toInt(),
                    name = accountName,
                    currency = Currency(id = currencyId.toInt(), name = accountName, sign = currencySign),
                    description = accountDesceription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.asFlow().mapToOne(Dispatchers.IO)
    }

    override suspend fun getAverageTransactionValue(): Flow<Double> {
        return transactionsQueries.getAvgExpenseOfTransactionType("INCOME").asFlow().mapToOne(Dispatchers.IO)
            .map { it.averageAmount ?: 0.0 }
    }

    override suspend fun getFilteredTransactions(
        transactions: List<Transaction.Income>,
        filter: TransactionFilter
    ): Flow<List<Transaction.Income>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTransaction(transaction: Transaction.Income) {
        Napier.d(transaction.toString(), tag = "UPDATED2")

        transactionsQueries.updateTransaction(
            name = transaction.name,
            description = transaction.description,
            amount = transaction.amount,
            timestamp = transaction.timestamp,
            account_id = transaction.account.id.toLong(), // Assuming account_id is a Long
            id = transaction.id.toLong() // Assuming id is a Long
        )
    }
}