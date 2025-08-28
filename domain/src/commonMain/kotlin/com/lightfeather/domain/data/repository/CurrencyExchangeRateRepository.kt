package com.lightfeather.domain.data.repository

import com.lightfeather.domain.domain.Currency
import com.lightfeather.domain.domain.CurrencyExchangeRate
import com.lightfeather.domain.domain.DomainResult
import kotlinx.coroutines.flow.Flow


interface CurrencyExchangeRateRepository {

    suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Int>

    suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>) : DomainResult<Boolean>

    suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate) : DomainResult<Boolean>
    suspend fun getCurrencyExchangeRateById(id: Int, toId: Int): DomainResult<CurrencyExchangeRate>

    fun getExchangeRatesOfCurrency(currency: Currency): DomainResult<Flow<List<CurrencyExchangeRate>>>

    fun getAllCurrenciesExchangeRates(): DomainResult<Flow<List<CurrencyExchangeRate>>>
}