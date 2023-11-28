package data.local

import com.lightfeather.core.data.datasource.transactions.TransferDatasource
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.domain.transaction.TransactionFilter
import com.lightfeather.masarify.database.BankAccountsQueries
import com.lightfeather.masarify.database.TransactionsQueries


class TransferDatasourceLocalImp(
    private val queries: TransactionsQueries,
    private val accountsQueries: BankAccountsQueries
) : TransferDatasource {


    override suspend fun createTransaction(transaction: Transaction.Transfer) {
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
        }
    }

    override suspend fun updateTransaction(transaction: Transaction.Transfer) {

    }

    override suspend fun deleteTransaction(transaction: Transaction.Transfer): Boolean {
        queries.deleteTransaction(transaction.id.toLong())
        return true
    }

    override suspend fun getAllTransactions(): List<Transaction.Transfer> {
        return queries.getAllTransactionsOfType("TRANSFER") { id, transactionName, transactionDescription, amount, timestamp, receiverAccountId, fee, accountId, accountName, accountDescription, accountBalance, accountColor, accountLogo, currencyId, currencyName, currencySign ->
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
        }.executeAsList()
    }

    override suspend fun getTransactionById(id: Int): Transaction.Transfer {
        return queries.getTransactionOfTypeById(
            "TRANSFER",
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
        }.executeAsOne()
    }

    override suspend fun getMinTransaction(): Transaction.Transfer {
        return queries.getMinExpenseOfTransactionType("TRANSFER") { id: Long, transactionName: String?, transactionDescription: String?, amount: Double?, timestamp: Long?, receiverAccountId: Long?, fee: Double?, accountId: Long, accountName: String, accountDescription: String?, accountBalance: Double, accountColor: String, accountLogo: String?, currencyId: Long, currencyName: String, currencySign: String ->
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
        }.executeAsOne()
    }

    override suspend fun getMaxTransaction(): Transaction.Transfer {
        return queries.getMaxExpenseOfTransactionType("TRANSFER") { id: Long, transactionName: String?, transactionDescription: String?, amount: Double?, timestamp: Long?, receiverAccountId: Long?, fee: Double?, accountId: Long, accountName: String, accountDescription: String?, accountBalance: Double, accountColor: String, accountLogo: String?, currencyId: Long, currencyName: String, currencySign: String ->
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
        }.executeAsOne()

    }

    override suspend fun getAverageTransactionValue(): Double {
        return queries.getAvgExpenseOfTransactionType("TRANSFER").executeAsOne().averageAmount ?: 0.0
    }

    override suspend fun getFilteredTransactions(
        transactions: List<Transaction.Transfer>,
        filter: TransactionFilter
    ): List<Transaction.Transfer> {
        TODO("Not yet implemented")
    }

}