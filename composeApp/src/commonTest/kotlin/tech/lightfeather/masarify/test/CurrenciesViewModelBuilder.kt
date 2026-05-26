package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.CreateCurrency
import tech.lightfeather.domain.usecase.DeleteCurrency
import tech.lightfeather.domain.usecase.GetAllCurrencies
import tech.lightfeather.domain.usecase.GetExchangeRatesOfCurrency
import tech.lightfeather.domain.usecase.UpdateCurrency
import tech.lightfeather.domain.usecase.UpdateCurrencyExchangeRates
import tech.lightfeather.masarify.page.currencies.CurrenciesPageViewModel

internal fun buildCurrenciesViewModel(): CurrenciesPageViewModel {
    val currencyRepo = FakeCurrencyRepository()
    val exchangeRateRepo = FakeCurrencyExchangeRateRepository()
    val userRepo = FakeUserRepository()
    val syncHelper = buildSyncEnqueueHelper()
    return CurrenciesPageViewModel(
        getAllCurrencies = GetAllCurrencies(currencyRepo),
        createCurrency = CreateCurrency(currencyRepo, exchangeRateRepo, syncHelper),
        updateCurrency = UpdateCurrency(currencyRepo, syncHelper),
        deleteCurrency = DeleteCurrency(currencyRepo, syncHelper),
        getExchangeRatesOfCurrency = GetExchangeRatesOfCurrency(exchangeRateRepo),
        updateCurrencyExchangeRates = UpdateCurrencyExchangeRates(exchangeRateRepo),
        userRepository = userRepo,
    )
}
