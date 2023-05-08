package data.repository

import data.datasource.AccountDatasource
import domain.Account

class AccountRepository (private val datasource: AccountDatasource){
    fun createAccount(account: Account): Int = datasource.createAccount(account)

    fun updateAccountName(account: Account) = datasource.updateAccountName(account)

    fun deleteAccount(account: Account): Boolean = datasource.deleteAccount(account)

    fun getAccounts() = datasource.getAccounts()

    fun getAccountById(id: Int): Account = datasource.getAccountById(id)
}