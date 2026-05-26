package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeCurrencyExchangeRateRepository
import tech.lightfeather.domain.fake.FakeCurrencyRepository
import tech.lightfeather.domain.fake.noSyncHelper
import tech.lightfeather.domain.model.Currency
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val testCurrency = Currency(name = "USD", sign = "$", id = -1)

class CurrencyUseCaseTest {
    @Test
    fun createCurrencySuccessStoresAndReturnsId() =
        runTest {
            val repo = FakeCurrencyRepository()
            val result = CreateCurrency(repo, FakeCurrencyExchangeRateRepository(), noSyncHelper())(testCurrency)
            assertTrue(result.isSuccess)
            val id = result.getOrNull()!!
            assertTrue(id > 0)
        }

    @Test
    fun createCurrencyFailureReturnsDomainResultFailure() =
        runTest {
            val repo = FakeCurrencyRepository(shouldFail = true)
            val result = CreateCurrency(repo, FakeCurrencyExchangeRateRepository(), noSyncHelper())(testCurrency)
            assertTrue(result.isFailure)
        }

    @Test
    fun updateCurrencySuccessReturnsDomainResultSuccess() =
        runTest {
            val repo = FakeCurrencyRepository()
            val id =
                CreateCurrency(repo, FakeCurrencyExchangeRateRepository(), noSyncHelper())
                    .invoke(testCurrency)
                    .getOrNull()!!
            val updated = testCurrency.copy(id = id, name = "Euro")
            val result = UpdateCurrency(repo, noSyncHelper())(updated)
            assertTrue(result.isSuccess)
            assertEquals("Euro", repo.store[id]?.name)
        }

    @Test
    fun deleteCurrencySuccessRemovesFromStore() =
        runTest {
            val repo = FakeCurrencyRepository()
            val id =
                CreateCurrency(repo, FakeCurrencyExchangeRateRepository(), noSyncHelper())
                    .invoke(testCurrency)
                    .getOrNull()!!
            val stored = repo.store[id]!!
            val result = DeleteCurrency(repo, noSyncHelper())(stored)
            assertTrue(result.isSuccess)
            assertTrue(repo.store[id] == null)
        }

    @Test
    fun getAllCurrenciesFlowReflectsCreatedCurrency() =
        runTest {
            val repo = FakeCurrencyRepository()
            CreateCurrency(repo, FakeCurrencyExchangeRateRepository(), noSyncHelper())(testCurrency)
            val result = GetAllCurrencies(repo)()
            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertEquals(1, list.size)
            assertEquals("USD", list.first().name)
        }
}
