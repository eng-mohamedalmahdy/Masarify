package com.lightfeather.core.data.datasource

import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Accounts

interface AccountDatasource {

    fun createAccount(account: Account): Int
    fun updateAccountName(account: Account)
    fun deleteAccount(account: Account): Boolean
    fun getAccounts(): Accounts
    fun getAccountById(id: Int): Account


}