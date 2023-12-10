package com.lightfeather.core.usecase

import com.lightfeather.core.data.repository.CurrencyExchangeRateRepository
import com.lightfeather.core.data.repository.CurrencyRepository
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.CurrencyExchangeRate
import kotlinx.coroutines.flow.first

class CreateCurrency(
    private val currencyRepository: CurrencyRepository,
    private val exchangeRateRepository: CurrencyExchangeRateRepository
) {
    suspend operator fun invoke(currency: Currency): Int {

        val id = currencyRepository.createCurrency(currency)
        val currencies = currencyRepository.getAllCurrencies().first()
        currencies.forEach {
            exchangeRateRepository.createCurrencyExchangeRate(CurrencyExchangeRate(it, currency.copy(id = id), 1.0))
            exchangeRateRepository.createCurrencyExchangeRate(CurrencyExchangeRate(currency.copy(id = id), it, 1.0))
        }
        exchangeRateRepository.createCurrencyExchangeRate(
            CurrencyExchangeRate(currency.copy(id = id), currency.copy(id = id), 1.0)
        )
        return id
    }
}

class UpdateCurrency(private val currencyRepository: CurrencyRepository) {
    suspend operator fun invoke(currency: Currency) = currencyRepository.updateCurrency(currency)
}

class DeleteCurrency(private val currencyRepository: CurrencyRepository) {
    suspend operator fun invoke(currency: Currency) = currencyRepository.deleteCurrency(currency)
}

class GetCurrencyById(private val currencyRepository: CurrencyRepository) {
    suspend operator fun invoke(id: Int) = currencyRepository.getCurrencyById(id)
}


class GetAllCurrencies(private val currencyRepository: CurrencyRepository) {
    suspend operator fun invoke() = currencyRepository.getAllCurrencies()
}





