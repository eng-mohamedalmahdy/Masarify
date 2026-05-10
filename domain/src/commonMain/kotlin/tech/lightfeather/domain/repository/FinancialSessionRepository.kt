package tech.lightfeather.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.FinancialSession

interface FinancialSessionRepository {
    suspend fun createSession(session: FinancialSession): DomainResult<Int>

    suspend fun updateSession(session: FinancialSession): DomainResult<Boolean>

    suspend fun deleteSession(sessionId: Int): DomainResult<Boolean>

    suspend fun getAllSessions(): DomainResult<Flow<List<FinancialSession>>>

    suspend fun getSessionById(id: Int): DomainResult<FinancialSession>

    suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit>

    suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?>

    suspend fun getUnsyncedIds(): DomainResult<List<Int>>
}
