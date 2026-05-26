package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeFinancialSessionRepository
import tech.lightfeather.domain.fake.noSyncHelper
import kotlin.test.Test
import kotlin.test.assertTrue

class FinancialSessionUseCaseTest {
    @Test
    fun getAllSessionsReturnsDomainResultSuccess() =
        runTest {
            val result = GetAllFinancialSessions(FakeFinancialSessionRepository())()
            assertTrue(result.isSuccess)
        }

    @Test
    fun getAllSessionsFlowIsEmptyInitially() =
        runTest {
            val result = GetAllFinancialSessions(FakeFinancialSessionRepository())()
            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertTrue(list.isEmpty())
        }

    @Test
    fun getSessionByIdReturnsFailureWhenNotFound() =
        runTest {
            val result = GetFinancialSessionById(FakeFinancialSessionRepository())(999)
            assertTrue(result.isFailure)
        }

    @Test
    fun deleteSessionSuccessReturnsDomainResultSuccess() =
        runTest {
            val result = DeleteFinancialSession(FakeFinancialSessionRepository(), noSyncHelper())(1)
            assertTrue(result.isSuccess)
        }
}
