package com.lightfeather.core.data.repository

import com.lightfeather.core.data.datasource.CurrencyExchangeRateDatasource
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.CurrencyExchangeRate

class CurrencyExchangeRateRepository(private val datasource: CurrencyExchangeRateDatasource) {

    suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): Int =
        datasource.createCurrencyExchangeRate(rate)

    suspend fun updateCurrencyExchangeRate(rate: CurrencyExchangeRate) = datasource.updateCurrencyExchangeRate(rate)

    suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): Boolean =
        datasource.deleteCurrencyExchangeRate(rate)

    suspend fun getAllCurrenciesExchangeRates(): List<List<CurrencyExchangeRate>> =
        datasource.getAllCurrenciesExchangeRates()

    suspend fun getCurrencyExchangeRateById(id: Int): CurrencyExchangeRate = datasource.getCurrencyExchangeRateById(id)

    suspend fun getExchangeRatesOfCurrency(currency: Currency): List<CurrencyExchangeRate> =
        datasource.getExchangeRatesOfCurrency(currency)
}