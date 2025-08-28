package com.lightfeather.domain.data.repository

import com.lightfeather.domain.domain.Currency
import com.lightfeather.domain.domain.CurrencyExchangeRate
import kotlinx.coroutines.flow.Flow


interface CurrencyExchangeRateRepository {

    suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): Int

    suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>)

    suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): Boolean
    suspend fun getCurrencyExchangeRateById(id: Int, toId: Int): CurrencyExchangeRate

    fun getExchangeRatesOfCurrency(currency: Currency): Flow<List<CurrencyExchangeRate>>

    fun getAllCurrenciesExchangeRates(): Flow<List<CurrencyExchangeRate>>
}