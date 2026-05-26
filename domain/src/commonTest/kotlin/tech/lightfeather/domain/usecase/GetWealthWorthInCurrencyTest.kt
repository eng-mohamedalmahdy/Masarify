package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeAccountRepository
import tech.lightfeather.domain.fake.FakeCurrencyExchangeRateRepository
import tech.lightfeather.domain.fake.FakeCurrencyRepository
import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.CurrencyExchangeRate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val currencyA = Currency(name = "USD", sign = "$", id = 1)
private val currencyB = Currency(name = "EUR", sign = "€", id = 2)

private fun makeAccount(
    id: Int,
    currency: Currency,
    balance: Double,
) = Account(
    name = "Account $id",
    currency = currency,
    description = null,
    balance = balance,
    color = "#FFFFFF",
    logo = null,
    id = id,
)

class GetWealthWorthInCurrencyTest {
    @Test
    fun returnsEmptyWorthListWhenNoUsedCurrencies() =
        runTest {
            val useCase =
                GetWealthWorthInCurrency(
                    accountRepository = FakeAccountRepository(),
                    currencyExchangeRateRepository = FakeCurrencyExchangeRateRepository(),
                    currencyRepository = FakeCurrencyRepository(),
                )

            val result = useCase()
            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertTrue(list.isEmpty())
        }

    @Test
    fun returnsSingleCurrencyWorthEqualsAccountBalance() =
        runTest {
            val accountRepo = FakeAccountRepository()
            val currencyRepo = FakeCurrencyRepository()
            accountRepo.store[1] = makeAccount(id = 1, currency = currencyA, balance = 500.0)
            currencyRepo.store[1] = currencyA

            val useCase =
                GetWealthWorthInCurrency(
                    accountRepository = accountRepo,
                    currencyExchangeRateRepository = FakeCurrencyExchangeRateRepository(),
                    currencyRepository = currencyRepo,
                )

            val result = useCase()
            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertEquals(1, list.size)
            assertEquals(500.0, list.first().worth)
            assertEquals(currencyA.id, list.first().currency.id)
        }

    @Test
    fun sumsMultipleAccountsInSameCurrency() =
        runTest {
            val accountRepo = FakeAccountRepository()
            val currencyRepo = FakeCurrencyRepository()
            accountRepo.store[1] = makeAccount(id = 1, currency = currencyA, balance = 300.0)
            accountRepo.store[2] = makeAccount(id = 2, currency = currencyA, balance = 200.0)
            currencyRepo.store[1] = currencyA

            val useCase =
                GetWealthWorthInCurrency(
                    accountRepository = accountRepo,
                    currencyExchangeRateRepository = FakeCurrencyExchangeRateRepository(),
                    currencyRepository = currencyRepo,
                )

            val result = useCase()
            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertEquals(1, list.size)
            assertEquals(500.0, list.first().worth)
        }

    @Test
    fun convertsBalanceUsingExchangeRate() =
        runTest {
            val accountRepo = FakeAccountRepository()
            val currencyRepo = FakeCurrencyRepository()
            val rateRepo = FakeCurrencyExchangeRateRepository()

            accountRepo.store[1] = makeAccount(id = 1, currency = currencyA, balance = 100.0)
            currencyRepo.store[1] = currencyA
            currencyRepo.store[2] = currencyB
            rateRepo.store.add(CurrencyExchangeRate(from = currencyA, to = currencyB, rate = 2.0))

            val useCase =
                GetWealthWorthInCurrency(
                    accountRepository = accountRepo,
                    currencyExchangeRateRepository = rateRepo,
                    currencyRepository = currencyRepo,
                )

            val result = useCase()
            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertEquals(2, list.size)

            val worthA = list.find { it.currency.id == currencyA.id }!!
            val worthB = list.find { it.currency.id == currencyB.id }!!
            assertEquals(100.0, worthA.worth)
            assertEquals(200.0, worthB.worth)
        }
}
