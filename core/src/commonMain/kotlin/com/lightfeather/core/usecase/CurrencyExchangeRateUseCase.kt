package com.lightfeather.core.usecase

import com.lightfeather.core.data.repository.CurrencyExchangeRateRepository
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.CurrencyExchangeRate


class CreateCurrencyExchangeRate(private val repository: CurrencyExchangeRateRepository) {
    suspend operator fun invoke(currencyExchangeRate: CurrencyExchangeRate) =
        repository.createCurrencyExchangeRate(currencyExchangeRate)
}

class UpdateCurrencyExchangeRates(private val repository: CurrencyExchangeRateRepository) {
    suspend operator fun invoke(currencyExchangeRates: List<List<CurrencyExchangeRate>>) =
        repository.updateCurrencyExchangeRates(currencyExchangeRates)
}

class DeleteCurrencyExchangeRate(private val repository: CurrencyExchangeRateRepository) {
    suspend operator fun invoke(currencyExchangeRate: CurrencyExchangeRate) =
        repository.deleteCurrencyExchangeRate(currencyExchangeRate)
}

class GetAllCurrenciesExchangeRates(private val repository: CurrencyExchangeRateRepository) {
    suspend operator fun invoke() =
        repository.getAllCurrenciesExchangeRates()
}

class GetCurrencyExchangeRateById(private val repository: CurrencyExchangeRateRepository) {
    suspend operator fun invoke(id: Int) =
        repository.getCurrencyExchangeRateById(id)
}

class GetExchangeRatesOfCurrency(private val repository: CurrencyExchangeRateRepository) {
    suspend operator fun invoke(currency: Currency) =
        repository.getExchangeRatesOfCurrency(currency)
}












