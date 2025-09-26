package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.CurrencyExchangeRate
import com.lightfeather.domain.repository.CurrencyExchangeRateRepository

class CreateCurrencyExchangeRate(
    private val repository: CurrencyExchangeRateRepository,
) {
    suspend operator fun invoke(currencyExchangeRate: CurrencyExchangeRate) =
        repository.createCurrencyExchangeRate(currencyExchangeRate)
}

class UpdateCurrencyExchangeRates(
    private val repository: CurrencyExchangeRateRepository,
) {
    suspend operator fun invoke(currencyExchangeRates: List<List<CurrencyExchangeRate>>) =
        repository.updateCurrencyExchangeRates(currencyExchangeRates)
}

class DeleteCurrencyExchangeRate(
    private val repository: CurrencyExchangeRateRepository,
) {
    suspend operator fun invoke(currencyExchangeRate: CurrencyExchangeRate) =
        repository.deleteCurrencyExchangeRate(currencyExchangeRate)
}

class GetAllCurrenciesExchangeRates(
    private val repository: CurrencyExchangeRateRepository,
) {

    operator fun invoke() = repository.getAllCurrenciesExchangeRates()
}

class GetCurrencyExchangeRateById(
    private val repository: CurrencyExchangeRateRepository,
) {
    suspend operator fun invoke(
        fromId: Int,
        toId: Int,
    ) = repository.getCurrencyExchangeRateById(fromId, toId)
}

class GetExchangeRatesOfCurrency(
    private val repository: CurrencyExchangeRateRepository,
) {
    suspend operator fun invoke(currency: Currency) = repository.getExchangeRatesOfCurrency(currency)
}
