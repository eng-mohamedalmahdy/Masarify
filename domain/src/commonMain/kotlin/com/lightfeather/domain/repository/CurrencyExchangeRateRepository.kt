package com.lightfeather.domain.repository

import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.CurrencyExchangeRate
import com.lightfeather.domain.model.DomainResult
import kotlinx.coroutines.flow.Flow


interface CurrencyExchangeRateRepository {

    suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Int>

    suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>) : DomainResult<Boolean>

    suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate) : DomainResult<Boolean>
    suspend fun getCurrencyExchangeRateById(id: Int, toId: Int): DomainResult<CurrencyExchangeRate>

    fun getExchangeRatesOfCurrency(currency: Currency): DomainResult<Flow<List<CurrencyExchangeRate>>>

    fun getAllCurrenciesExchangeRates(): DomainResult<Flow<List<CurrencyExchangeRate>>>
}