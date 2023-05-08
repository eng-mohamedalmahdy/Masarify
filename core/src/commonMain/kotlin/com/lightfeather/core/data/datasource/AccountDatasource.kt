package data.datasource

import domain.Account
import domain.Accounts

interface AccountDatasource {

    fun createAccount(account: Account): Int
    fun updateAccountName(account: Account)
    fun deleteAccount(account: Account): Boolean
    fun getAccounts(): Accounts
    fun getAccountById(id: Int): Account


}