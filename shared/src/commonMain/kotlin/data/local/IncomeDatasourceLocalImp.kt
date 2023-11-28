package data.local

import com.lightfeather.core.data.datasource.transactions.IncomeDatasource
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Category
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter
import com.lightfeather.masarify.database.TransactionsQueries

class IncomeDatasourceLocalImp(private val transactionsQueries: TransactionsQueries) : IncomeDatasource {
    override suspend fun createTransaction(transaction: Transaction.Income) {
        with(transaction) {
            transactionsQueries.insertTransaction(
                "INCOME", name, description, amount, timestamp, account.id.toLong()
            )
            val id = transactionsQueries.selectLastInsertedRowId().executeAsOne()
            transactionsQueries.insertTransactionCategory(id, source.id.toLong())
        }
    }

    override suspend fun updateTransaction(transaction: Transaction.Income) {
        with(transaction) {
            transactionsQueries.updateTransaction(
                name,
                description,
                amount,
                timestamp,
                account.id.toLong(),
                id.toLong()
            )
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction.Income): Boolean {
        transactionsQueries.deleteTransaction(transaction.id.toLong())
        return true
    }

    override suspend fun getAllTransactions(): List<Transaction.Income> {
        return transactionsQueries.getAllTransactionsOfType("INCOME") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDescription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Income(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                source = transactionsQueries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, description: String, color: String, icon: String ->
                    Category(id.toInt(), name, description, color, icon)
                }.executeAsOne(),
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
        }.executeAsList()
    }

    override suspend fun getTransactionById(id: Int): Transaction.Income {
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
                }.executeAsOne(),
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

    override suspend fun getMinTransaction(): Transaction.Income {
        return transactionsQueries.getMinExpenseOfTransactionType("INCOME") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDesceription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Income(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                source = transactionsQueries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, desceription: String, color: String, icon: String ->
                    Category(id.toInt(), name, desceription, color, icon)
                }.executeAsOne(),
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

    override suspend fun getMaxTransaction(): Transaction.Income {
        return transactionsQueries.getMaxExpenseOfTransactionType("INCOME") { id, transactionName, transactionDescription, amount, timestamp, _, _, accountId, accountName, accountDesceription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
            Transaction.Income(
                id = id.toInt(),
                name = transactionName ?: "",
                description = transactionDescription ?: "",
                source = transactionsQueries.getCategoriesForTransactionByTransactionId(id) { id: Long, name: String, description: String, color: String, icon: String ->
                    Category(id.toInt(), name, description, color, icon)
                }.executeAsOne(),
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
        return transactionsQueries.getAvgExpenseOfTransactionType("INCOME").executeAsOne().averageAmount ?: 0.0
    }

    override suspend fun getFilteredTransactions(
        transactions: List<Transaction.Income>,
        filter: TransactionFilter
    ): List<Transaction.Income> {
        TODO("Not yet implemented")
    }
}