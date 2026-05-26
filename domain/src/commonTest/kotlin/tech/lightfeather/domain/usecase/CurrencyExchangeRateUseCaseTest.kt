package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeCurrencyExchangeRateRepository
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.CurrencyExchangeRate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val usd = Currency(name = "USD", sign = "$", id = 1)
private val eur = Currency(name = "EUR", sign = "€", id = 2)
private val testRate = CurrencyExchangeRate(from = usd, to = eur, rate = 0.92)

class CurrencyExchangeRateUseCaseTest {
    @Test
    fun createRateSuccessStoresAndReturnsId() =
        runTest {
            val repo = FakeCurrencyExchangeRateRepository()
            val result = CreateCurrencyExchangeRate(repo)(testRate)
            assertTrue(result.isSuccess)
            assertEquals(1, repo.store.size)
        }

    @Test
    fun createRateFailureReturnsDomainResultFailure() =
        runTest {
            val repo = FakeCurrencyExchangeRateRepository(shouldFail = true)
            val result = CreateCurrencyExchangeRate(repo)(testRate)
            assertTrue(result.isFailure)
            assertTrue(repo.store.isEmpty())
        }

    @Test
    fun getAllRatesFlowReflectsCreatedRate() =
        runTest {
            val repo = FakeCurrencyExchangeRateRepository()
            CreateCurrencyExchangeRate(repo)(testRate)
            val result = GetAllCurrenciesExchangeRates(repo)()
            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertEquals(1, list.size)
            assertEquals(0.92, list.first().rate)
        }

    @Test
    fun getRatesOfCurrencyFiltersCorrectly() =
        runTest {
            val repo = FakeCurrencyExchangeRateRepository()
            CreateCurrencyExchangeRate(repo)(testRate)
            CreateCurrencyExchangeRate(repo)(CurrencyExchangeRate(eur, usd, 1.09))
            val result = GetExchangeRatesOfCurrency(repo)(usd)
            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertEquals(1, list.size)
            assertEquals(eur.id, list.first().to.id)
        }

    @Test
    fun updateRatesSuccessReturnsDomainResultSuccess() =
        runTest {
            val repo = FakeCurrencyExchangeRateRepository()
            val result = UpdateCurrencyExchangeRates(repo)(listOf(listOf(testRate)))
            assertTrue(result.isSuccess)
        }
}
