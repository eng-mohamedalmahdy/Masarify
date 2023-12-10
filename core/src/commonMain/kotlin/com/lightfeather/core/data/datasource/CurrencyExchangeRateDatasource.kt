package com.lightfeather.core.data.datasource

import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.CurrencyExchangeRate
import kotlinx.coroutines.flow.Flow


interface CurrencyExchangeRateDatasource {

    suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): Int
    suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>)
    suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): Boolean
    suspend fun getAllCurrenciesExchangeRates(): Flow<List<List<CurrencyExchangeRate>>>
    suspend fun getCurrencyExchangeRateById(fromId: Int,toId:Int): CurrencyExchangeRate
    suspend fun getExchangeRatesOfCurrency(currency: Currency): Flow<List<CurrencyExchangeRate>>

}