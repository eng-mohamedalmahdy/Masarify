package com.lightfeather.core.data.repository

import com.lightfeather.core.data.datasource.CurrencyExchangeRateDatasource
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.CurrencyExchangeRate

class CurrencyExchangeRateRepository(private val datasource: CurrencyExchangeRateDatasource) {

    suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): Int =
        datasource.createCurrencyExchangeRate(rate)

    suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>) =
        datasource.updateCurrencyExchangeRates(rates)

    suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): Boolean =
        datasource.deleteCurrencyExchangeRate(rate)

    suspend fun getAllCurrenciesExchangeRates() =
        datasource.getAllCurrenciesExchangeRates()

    suspend fun getCurrencyExchangeRateById(id: Int, toId: Int): CurrencyExchangeRate = datasource.getCurrencyExchangeRateById(id,toId)

    suspend fun getExchangeRatesOfCurrency(currency: Currency) =
        datasource.getExchangeRatesOfCurrency(currency)
}