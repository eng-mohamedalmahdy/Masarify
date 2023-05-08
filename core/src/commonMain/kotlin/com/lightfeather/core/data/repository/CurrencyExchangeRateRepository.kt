package data.repository

import data.datasource.CurrencyExchangeRateDatasource
import domain.Currency
import domain.CurrencyExchangeRate

class CurrencyExchangeRateRepository(private val datasource: CurrencyExchangeRateDatasource) {

    fun createCurrencyExchangeRate(rate: CurrencyExchangeRate): Int = datasource.createCurrencyExchangeRate(rate)

    fun updateCurrencyExchangeRate(rate: CurrencyExchangeRate) = datasource.updateCurrencyExchangeRate(rate)

    fun deleteCurrencyExchangeRate(rate: CurrencyExchangeRate): Boolean = datasource.deleteCurrencyExchangeRate(rate)

    fun getAllCurrenciesExchangeRates(): List<List<CurrencyExchangeRate>> = datasource.getAllCurrenciesExchangeRates()

    fun getCurrencyExchangeRateById(id: Int): CurrencyExchangeRate = datasource.getCurrencyExchangeRateById(id)

    fun getExchangeRatesOfCurrency(currency: Currency): List<CurrencyExchangeRate> =
        datasource.getExchangeRatesOfCurrency(currency)
}