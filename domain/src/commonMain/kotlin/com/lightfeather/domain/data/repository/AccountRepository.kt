package com.lightfeather.domain.data.repository

import com.lightfeather.domain.domain.Account
import com.lightfeather.domain.domain.DomainResult
import kotlinx.coroutines.flow.Flow


interface AccountRepository {
    suspend fun createAccount(account: Account): DomainResult<Int>

    suspend fun updateAccount(account: Account): DomainResult<Boolean>

    suspend fun deleteAccount(account: Account): DomainResult<Boolean>

    suspend fun getAccountById(id: Int): DomainResult<Account>

    fun getAccounts(): DomainResult<Flow<List<Account>>>
}