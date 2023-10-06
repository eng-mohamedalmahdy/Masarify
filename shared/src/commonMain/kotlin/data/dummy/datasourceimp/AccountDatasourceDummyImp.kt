package data.dummy.datasourceimp

import com.lightfeather.core.data.datasource.AccountDatasource
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Accounts
import data.dummy.model.DummyDomainModelsProviders

object AccountDatasourceDummyImp : AccountDatasource {
    override fun createAccount(account: Account): Int {
        TODO("Not yet implemented")
    }

    override fun updateAccountName(account: Account) {
        TODO("Not yet implemented")
    }

    override fun deleteAccount(account: Account): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAccounts(): Accounts {
       return listOf(DummyDomainModelsProviders.bankAccount)
    }

    override fun getAccountById(id: Int): Account {
        TODO("Not yet implemented")
    }
}