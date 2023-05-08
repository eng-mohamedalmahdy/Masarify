package data.repository

import data.datasource.CurrencyDatasource
import domain.Currency

class CurrencyRepository (private val datasource: CurrencyDatasource){
    fun createCurrency(currency: Currency): Int = datasource.createCurrency(currency)

    fun updateCurrency(currency: Currency) = datasource.updateCurrency(currency)

    fun deleteCurrency(currency: Currency): Boolean = datasource.deleteCurrency(currency)

    fun getAllCurrencies(): List<Currency> = datasource.getAllCurrencies()

    fun getCurrencyById(id: Int): Currency = datasource.getCurrencyById(id)
}