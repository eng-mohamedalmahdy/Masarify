package com.lightfeather.core.data.datasource

import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.CurrencyExchangeRate


interface CurrencyExchangeRateDatasource {

    suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): Int
    suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>)
    suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): Boolean
    suspend fun getAllCurrenciesExchangeRates(): List<List<CurrencyExchangeRate>>
    suspend fun getCurrencyExchangeRateById(id: Int): CurrencyExchangeRate
    suspend fun getExchangeRatesOfCurrency(currency: Currency): List<CurrencyExchangeRate>

}