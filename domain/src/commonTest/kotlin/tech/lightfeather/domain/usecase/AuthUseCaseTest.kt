package tech.lightfeather.domain.usecase

import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeAuthRepository
import tech.lightfeather.domain.fake.FakeBackupRepository
import tech.lightfeather.domain.fake.FakeUserRepository
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthUseCaseTest {
    @Test
    fun loginSuccessReturnsDomainResultSuccess() =
        runTest {
            val result = LoginUseCase(FakeAuthRepository())("user@example.com", "pass")
            assertTrue(result.isSuccess)
        }

    @Test
    fun loginFailureReturnsDomainResultFailure() =
        runTest {
            val result = LoginUseCase(FakeAuthRepository(shouldFail = true))("user@example.com", "pass")
            assertTrue(result.isFailure)
        }

    @Test
    fun registerSuccessReturnsDomainResultSuccess() =
        runTest {
            val result = RegisterUseCase(FakeAuthRepository())("Alice", "alice@example.com", "pass")
            assertTrue(result.isSuccess)
        }

    @Test
    fun logoutSuccessReturnsDomainResultSuccess() =
        runTest {
            val clearUseCase = ClearLocalDataUseCase(FakeBackupRepository(), FakeUserRepository())
            val result = LogoutUseCase(FakeAuthRepository(), clearUseCase)()
            assertTrue(result.isSuccess)
        }

    @Test
    fun verifyEmailSuccessReturnsDomainResultSuccess() =
        runTest {
            val result = VerifyEmailUseCase(FakeAuthRepository())("token123")
            assertTrue(result.isSuccess)
        }

    @Test
    fun forgotPasswordSuccessReturnsDomainResultSuccess() =
        runTest {
            val result = ForgotPasswordUseCase(FakeAuthRepository())("user@example.com")
            assertTrue(result.isSuccess)
        }

    @Test
    fun isAuthenticatedReturnsFalseByDefault() {
        val result = IsAuthenticatedUseCase(FakeAuthRepository())()
        assertFalse(result)
    }

    @Test
    fun isAuthenticatedReturnsTrueWhenAuthenticated() {
        val result = IsAuthenticatedUseCase(FakeAuthRepository(authenticated = true))()
        assertTrue(result)
    }
}
