package data.datasource

import domain.Currency
import domain.CurrencyExchangeRate


interface CurrencyExchangeRateDatasource {

    fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): Int
    fun updateCurrencyExchangeRate(rate: CurrencyExchangeRate)
    fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): Boolean
    fun getAllCurrenciesExchangeRates(): List<List<CurrencyExchangeRate>>
    fun getCurrencyExchangeRateById(id: Int): CurrencyExchangeRate
    fun getExchangeRatesOfCurrency(currency: Currency): List<CurrencyExchangeRate>

}