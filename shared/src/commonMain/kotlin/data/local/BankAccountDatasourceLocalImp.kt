package data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.lightfeather.core.data.datasource.AccountDatasource
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Accounts
import com.lightfeather.core.domain.Currency
import com.lightfeather.masarify.database.BankAccountsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    override fun updateAccount(account: Account) {
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

    override fun getAccounts(): Flow<Accounts> {
        return queries.getAllAccounts().asFlow().mapToList(Dispatchers.IO).map {
            it.map {
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