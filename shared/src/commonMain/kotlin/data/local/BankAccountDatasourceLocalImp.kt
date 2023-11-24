package data.local

import com.lightfeather.core.data.datasource.AccountDatasource
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Accounts
import com.lightfeather.core.domain.Currency
import com.lightfeather.masarify.database.BankAccountsQueries

class BankAccountDatasourceLocalImp(private val queries: BankAccountsQueries) : AccountDatasource {
    override fun createAccount(account: Account): Int {
        queries.insertBankAccount(
            account.currency.id.toLong(),
            account.name,
            account.description,
            account.balance,
            account.color,
            account.logo
        )
        return queries.selectLastInsertedRowId().executeAsOne().toInt()
    }

    override fun updateAccountName(account: Account) {
        with(account) {
            queries.updateAccount(
                balance = balance,
                name = name,
                color = color,
                logo = logo,
                id = id.toLong()

            )
        }
    }

    override fun deleteAccount(account: Account): Boolean {
        queries.deleteAccount(account.id.toLong())
        return true
    }

    override fun getAccounts(): Accounts {
        return queries.getAllAccounts().executeAsList().map {
            with(it) {
                Account(
                    id = id.toInt(),
                    name = name,
                    currency = Currency(
                        id = currencyId.toInt(),
                        name = currencyMame,
                        sign = sign
                    ),
                    description = desceription,
                    balance = balance,
                    color = color,
                    logo = logo.toString()
                )
            }
        }
    }

    override fun getAccountById(id: Int): Account? {
        return queries.getAccountById(id.toLong()).executeAsOneOrNull()?.let {
            with(it) {
                Account(
                    id = id,
                    name = name,
                    currency = Currency(currencyId.toInt(), currencyName, sign),
                    description = desceription,
                    balance = balance,
                    color = color,
                    logo = logo.toString()
                )
            }
        }
    }
}