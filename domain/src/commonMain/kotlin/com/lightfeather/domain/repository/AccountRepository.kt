package com.lightfeather.domain.repository

import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.DomainResult
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun createAccount(account: Account): DomainResult<Int>

    suspend fun updateAccount(account: Account): DomainResult<Boolean>

    suspend fun deleteAccount(account: Account): DomainResult<Boolean>

    suspend fun getAccountById(id: Int): DomainResult<Account>

    fun getAccounts(): DomainResult<Flow<List<Account>>>
}
