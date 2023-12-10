package com.lightfeather.core.data.datasource

import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Accounts
import kotlinx.coroutines.flow.Flow

interface AccountDatasource {

    fun createAccount(account: Account): Int
    fun updateAccount(account: Account)
    fun deleteAccount(account: Account): Boolean
    fun getAccounts(): Flow<Accounts>
    fun getAccountById(id: Int): Account?
}