package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.FinancialSession
import tech.lightfeather.domain.model.sync.AccountSnapshotSyncPayload
import tech.lightfeather.domain.model.sync.FinancialSessionSyncPayload
import tech.lightfeather.domain.repository.AccountRepository
import tech.lightfeather.domain.repository.FinancialSessionRepository

class CreateFinancialSession(
    private val sessionRepository: FinancialSessionRepository,
    private val accountRepository: AccountRepository,
    private val syncHelper: SyncEnqueueHelper,
) {
    suspend operator fun invoke(session: FinancialSession): DomainResult<Int> =
        sessionRepository.createSession(session).flatMapSuspend { sessionId ->
            val payload = session.copy(id = sessionId).toSyncPayload(accountRepository)
            if (payload != null) {
                syncHelper.enqueue(
                    "FINANCIAL_SESSION",
                    "CREATE",
                    payload,
                    FinancialSessionSyncPayload.serializer(),
                    sessionId,
                )
            } else if (syncHelper.isLoggedIn()) {
                syncHelper.fullSync()
            }
            session.accountSnapshots.forEach { snapshot ->
                val updatedAccount =
                    Account(
                        id = snapshot.account.id,
                        name = snapshot.account.name,
                        currency = snapshot.account.currency,
                        description = snapshot.account.description,
                        balance = snapshot.startingBalance,
                        color = snapshot.account.color,
                        logo = snapshot.account.logo,
                    )
                accountRepository.updateAccount(updatedAccount)
            }
            DomainResult.Success(sessionId)
        }
}

class UpdateFinancialSession(
    private val sessionRepository: FinancialSessionRepository,
    private val syncHelper: SyncEnqueueHelper,
) {
    suspend operator fun invoke(session: FinancialSession): DomainResult<Boolean> =
        sessionRepository.updateSession(session).also { result ->
            if (result.isSuccess) {
                syncHelper.enqueue(
                    "FINANCIAL_SESSION",
                    "UPDATE",
                    FinancialSessionSyncPayload(name = session.name, timestamp = session.timestamp),
                    FinancialSessionSyncPayload.serializer(),
                    session.id,
                )
            }
        }
}

class DeleteFinancialSession(
    private val sessionRepository: FinancialSessionRepository,
    private val syncHelper: SyncEnqueueHelper,
) {
    suspend operator fun invoke(sessionId: Int): DomainResult<Boolean> {
        val snapshot =
            if (syncHelper.isLoggedIn()) {
                sessionRepository.getSessionById(sessionId).getOrNull()
            } else {
                null
            }
        return sessionRepository.deleteSession(sessionId).also { result ->
            if (result.isSuccess && snapshot != null) {
                syncHelper.enqueue(
                    "FINANCIAL_SESSION",
                    "DELETE",
                    FinancialSessionSyncPayload(name = snapshot.name, timestamp = snapshot.timestamp),
                    FinancialSessionSyncPayload.serializer(),
                    sessionId,
                )
            }
        }
    }
}

class GetAllFinancialSessions(
    private val sessionRepository: FinancialSessionRepository,
) {
    suspend operator fun invoke() = sessionRepository.getAllSessions()
}

class GetFinancialSessionById(
    private val sessionRepository: FinancialSessionRepository,
) {
    suspend operator fun invoke(id: Int) = sessionRepository.getSessionById(id)
}

internal suspend fun FinancialSession.toSyncPayload(accountRepository: AccountRepository): FinancialSessionSyncPayload {
    val snapshots =
        accountSnapshots.mapNotNull { snap ->
            val accountId =
                accountRepository.getRemoteIdByLocalId(snap.account.id).getOrNull() ?: return@mapNotNull null
            AccountSnapshotSyncPayload(accountId, snap.startingBalance)
        }
    return FinancialSessionSyncPayload(name = name, timestamp = timestamp, accountSnapshots = snapshots)
}
