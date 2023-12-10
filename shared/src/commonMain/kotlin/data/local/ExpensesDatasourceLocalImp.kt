package data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.lightfeather.core.data.datasource.transactions.ExpensesDatasource
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

class ExpensesDatasourceLocalImp(private val queries: TransactionsQueries) : ExpensesDatasource {
    override suspend fun createTransaction(transaction: Transaction.Expense): DomainResult<Int> {
        with(transaction) {
            queries.insertTransaction("EXPENSE", name, description, amount, timestamp, account.id.toLong())

            val id = queries.selectLastInsertedRowId().executeAsOne()

            categories.forEach {
                queries.insertTransactionCategory(id, it.id.toLong())
            }
            return id.toInt().toDomainResult()
        }
    }


    override suspend fun deleteTransaction(transaction: Transaction): Boolean {
        queries.deleteTransaction(transaction.id.toLong())
        queries.deleteTransactionCategory(transaction.id.toLong())
        return true
    }

    override suspend fun getAllTransactions(): Flow<List<Transaction.Expense>> {
        return queries.getAllTransactionsOfType("EXPENSE") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDescription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Expense(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                categories = queries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, description: String, color: String, icon: String ->
                    Category(id.toInt(), name, description, color, icon)
                }.executeAsList(),
                amount = amount ?: 0.0,
                timestamp = timestamp ?: 0L,
                account = Account(
                    id = accountId.toInt(),
                    name = accountName,
                    currency = Currency(id = currencyId.toInt(), name = currencyName, sign = currencySign),
                    description = accountDescription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun getTransactionById(id: Int): DomainResult<Transaction.Expense> {
        return queries.getTransactionOfTypeById(
            "EXPENSE",
            id.toLong()
        ) { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDescription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Expense(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                categories = queries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, description: String, color: String, icon: String ->
                    Category(id.toInt(), name, description, color, icon)
                }.executeAsList(),
                amount = amount ?: 0.0,
                timestamp = timestamp ?: 0L,
                account = Account(
                    id = accountId.toInt(),
                    name = accountName,
                    currency = Currency(id = currencyId.toInt(), name = currencyName, sign = currencySign),
                    description = accountDescription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.executeAsOne().toDomainResult()
    }

    override suspend fun getMinTransaction(): Flow<Transaction.Expense> {
        return queries.getMinExpenseOfTransactionType("EXPENSE") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDescription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Expense(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                categories = queries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, description: String, color: String, icon: String ->
                    Category(id.toInt(), name, description, color, icon)
                }.executeAsList(),
                amount = amount ?: 0.0,
                timestamp = timestamp ?: 0L,
                account = Account(
                    id = accountId.toInt(),
                    name = accountName,
                    currency = Currency(id = currencyId.toInt(), name = currencyName, sign = currencySign),
                    description = accountDescription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.asFlow().mapToOne(Dispatchers.IO)
    }

    override suspend fun getMaxTransaction(): Flow<Transaction.Expense> {
        return queries.getMaxExpenseOfTransactionType("EXPENSE") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDescription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Expense(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                categories = queries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, description: String, color: String, icon: String ->
                    Category(id.toInt(), name, description, color, icon)
                }.executeAsList(),
                amount = amount ?: 0.0,
                timestamp = timestamp ?: 0L,
                account = Account(
                    id = accountId.toInt(),
                    name = accountName,
                    currency = Currency(id = currencyId.toInt(), name = currencyName, sign = currencySign),
                    description = accountDescription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.asFlow().mapToOne(Dispatchers.IO)
    }

    override suspend fun getAverageTransactionValue(): Flow<Double> {
        return queries.getAvgExpenseOfTransactionType("EXPENSE").asFlow().mapToOne(Dispatchers.IO)
            .map { it.averageAmount ?: 0.0 }
    }

    override suspend fun getFilteredTransactions(
        transactions: List<Transaction.Expense>, filter: TransactionFilter
    ): Flow<List<Transaction.Expense>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTransaction(transaction: Transaction.Expense) {
        Napier.d(transaction.toString(), tag = "UPDATED1")

        queries.updateTransaction(
            name = transaction.name,
            description = transaction.description,
            amount = transaction.amount,
            timestamp = transaction.timestamp,
            account_id = transaction.account.id.toLong(), // Assuming account_id is a Long
            id = transaction.id.toLong() // Assuming id is a Long
        )
    }
}