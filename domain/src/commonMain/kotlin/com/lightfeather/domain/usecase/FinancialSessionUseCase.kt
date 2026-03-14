package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.FinancialSession
import com.lightfeather.domain.repository.AccountRepository
import com.lightfeather.domain.repository.FinancialSessionRepository

class CreateFinancialSession(
    private val sessionRepository: FinancialSessionRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(session: FinancialSession): DomainResult<Int> =
        sessionRepository.createSession(session).flatMapSuspend { sessionId ->
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
) {
    suspend operator fun invoke(session: FinancialSession): DomainResult<Boolean> =
        sessionRepository.updateSession(session)
}

class DeleteFinancialSession(
    private val sessionRepository: FinancialSessionRepository,
) {
    suspend operator fun invoke(sessionId: Int): DomainResult<Boolean> = sessionRepository.deleteSession(sessionId)
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
