package com.lightfeather.core.data.repository

import com.lightfeather.core.data.datasource.AccountDatasource
import com.lightfeather.core.domain.Account

class AccountRepository (private val datasource: AccountDatasource){
    fun createAccount(account: Account): Int = datasource.createAccount(account)

    fun updateAccount(account: Account) = datasource.updateAccount(account)

    fun deleteAccount(account: Account): Boolean = datasource.deleteAccount(account)

    fun getAccounts() = datasource.getAccounts()

    fun getAccountById(id: Int): Account? = datasource.getAccountById(id)
}