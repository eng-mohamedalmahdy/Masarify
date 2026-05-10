package tech.lightfeather.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.DomainResult

interface AccountRepository {
    suspend fun createAccount(account: Account): DomainResult<Int>

    suspend fun updateAccount(account: Account): DomainResult<Boolean>

    suspend fun deleteAccount(account: Account): DomainResult<Boolean>

    suspend fun getAccountById(id: Int): DomainResult<Account>

    fun getAccounts(): DomainResult<Flow<List<Account>>>

    suspend fun setDefaultAccount(accountId: Int): DomainResult<Boolean>

    fun getDefaultAccount(): DomainResult<Flow<Account?>>

    suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit>

    suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?>

    suspend fun getRemoteIdByLocalId(localId: Int): DomainResult<Long?>

    suspend fun getUnsyncedIds(): DomainResult<List<Int>>
}
