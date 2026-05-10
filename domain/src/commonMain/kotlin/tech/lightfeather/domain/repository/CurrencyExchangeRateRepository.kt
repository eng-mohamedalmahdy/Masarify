package tech.lightfeather.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.CurrencyExchangeRate
import tech.lightfeather.domain.model.DomainResult

interface CurrencyExchangeRateRepository {
    suspend fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Int>

    suspend fun updateCurrencyExchangeRates(rates: List<List<CurrencyExchangeRate>>): DomainResult<Boolean>

    suspend fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): DomainResult<Boolean>

    suspend fun getCurrencyExchangeRateById(
        id: Int,
        toId: Int,
    ): DomainResult<CurrencyExchangeRate>

    fun getExchangeRatesOfCurrency(currency: Currency): DomainResult<Flow<List<CurrencyExchangeRate>>>

    fun getAllCurrenciesExchangeRates(): DomainResult<Flow<List<CurrencyExchangeRate>>>
}
