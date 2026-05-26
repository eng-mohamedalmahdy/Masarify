package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeAccountRepository
import tech.lightfeather.domain.fake.FakeCurrencyRepository
import tech.lightfeather.domain.fake.noSyncHelper
import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.Currency
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val testCurrency = Currency(name = "USD", sign = "$", id = 1)
private val testAccount =
    Account(
        name = "Checking",
        currency = testCurrency,
        description = null,
        balance = 1_000.0,
        color = "#123456",
        logo = null,
    )

class AccountUseCaseTest {
    @Test
    fun createAccountSuccessStoresAccountAndReturnsId() =
        runTest {
            val repo = FakeAccountRepository()
            val useCase = CreateAccount(repo, noSyncHelper(), FakeCurrencyRepository())

            val result = useCase(testAccount)

            assertTrue(result.isSuccess)
            val id = result.getOrNull()!!
            assertEquals("Checking", repo.store[id]?.name)
        }

    @Test
    fun createAccountFailurePropagatessError() =
        runTest {
            val repo = FakeAccountRepository(shouldFail = true)
            val useCase = CreateAccount(repo, noSyncHelper(), FakeCurrencyRepository())

            val result = useCase(testAccount)

            assertTrue(result.isFailure)
            assertTrue(repo.store.isEmpty())
        }

    @Test
    fun updateAccountSuccessUpdatesStoredAccount() =
        runTest {
            val repo = FakeAccountRepository()
            val id = CreateAccount(repo, noSyncHelper(), FakeCurrencyRepository())(testAccount).getOrNull()!!
            val updated = testAccount.copy(id = id, name = "Savings")

            val result = UpdateAccount(repo, noSyncHelper(), FakeCurrencyRepository())(updated)

            assertTrue(result.isSuccess)
            assertEquals("Savings", repo.store[id]?.name)
        }

    @Test
    fun deleteAccountSuccessRemovesAccountFromStore() =
        runTest {
            val repo = FakeAccountRepository()
            val id = CreateAccount(repo, noSyncHelper(), FakeCurrencyRepository())(testAccount).getOrNull()!!
            val stored = repo.store[id]!!

            val result = DeleteAccount(repo, noSyncHelper(), FakeCurrencyRepository())(stored)

            assertTrue(result.isSuccess)
            assertTrue(repo.store.isEmpty())
        }

    @Test
    fun getAllAccountsReturnsFlowContainingCreatedAccount() =
        runTest {
            val repo = FakeAccountRepository()
            CreateAccount(repo, noSyncHelper(), FakeCurrencyRepository())(testAccount)

            val result = GetAllAccounts(repo)()

            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertEquals(1, list.size)
            assertEquals("Checking", list.first().name)
        }

    @Test
    fun getAccountByIdReturnsTheStoredAccount() =
        runTest {
            val repo = FakeAccountRepository()
            val id = CreateAccount(repo, noSyncHelper(), FakeCurrencyRepository())(testAccount).getOrNull()!!

            val result = GetBankAccountById(repo)(id)

            assertTrue(result.isSuccess)
            assertEquals("Checking", result.getOrNull()?.name)
        }
}
