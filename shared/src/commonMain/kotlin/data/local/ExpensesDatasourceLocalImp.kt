package data.local

import com.lightfeather.core.data.datasource.transactions.ExpensesDatasource
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Category
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter
import com.lightfeather.masarify.MR.strings.amount
import com.lightfeather.masarify.MR.strings.description
import com.lightfeather.masarify.database.TransactionsQueries

class ExpensesDatasourceLocalImp(private val queries: TransactionsQueries) : ExpensesDatasource {
    override suspend fun createTransaction(transaction: Transaction.Expense) {
        with(transaction) {
            queries.insertTransaction("EXPENSE", name, description, amount, timestamp, account.id.toLong())

            val id = queries.selectLastInsertedRowId().executeAsOne()

            categories.forEach {
                queries.insertTransactionCategory(id, it.id.toLong())
            }
        }
    }

    override suspend fun updateTransaction(transaction: Transaction.Expense) {
        with(transaction) {
            queries.updateTransaction(name, description, amount, timestamp, account.id.toLong(), id.toLong())
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction.Expense): Boolean {
        queries.deleteTransaction(transaction.id.toLong())
        return true
    }

    override suspend fun getAllTransactions(): List<Transaction.Expense> {
        return queries.getAllTransactionsOfType("EXPENSE") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDesceription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
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
                    currency = Currency(id = currencyId.toInt(), name = accountName, sign = currencySign),
                    description = accountDesceription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.executeAsList()
    }

    override suspend fun getTransactionById(id: Int): Transaction.Expense {
        return queries.getTransactionOfTypeById(
            "EXPENSE",
            id.toLong()
        ) { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDescription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Expense(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                categories = queries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, desceription: String, color: String, icon: String ->
                    Category(id.toInt(), name, desceription, color, icon)
                }.executeAsList(),
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
        }.executeAsOne()
    }

    override suspend fun getMinTransaction(): Transaction.Expense {
        return queries.getMinExpenseOfTransactionType("EXPENSE") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDesceription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
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
                    currency = Currency(id = currencyId.toInt(), name = accountName, sign = currencySign),
                    description = accountDesceription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.executeAsOne()
    }

    override suspend fun getMaxTransaction(): Transaction.Expense {
        return queries.getMaxExpenseOfTransactionType("EXPENSE") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDesceription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
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
                    currency = Currency(id = currencyId.toInt(), name = accountName, sign = currencySign),
                    description = accountDesceription,
                    balance = accountBalance,
                    color = accountColor,
                    logo = accountLogo.toString()
                )
            )
        }.executeAsOne()
    }

    override suspend fun getAverageTransactionValue(): Double {
        return queries.getAvgExpenseOfTransactionType("EXPENSE").executeAsOne().averageAmount ?: 0.0
    }

    override suspend fun getFilteredTransactions(
        transactions: List<Transaction.Expense>, filter: TransactionFilter
    ): List<Transaction.Expense> {
        TODO("Not yet implemented")
    }
}