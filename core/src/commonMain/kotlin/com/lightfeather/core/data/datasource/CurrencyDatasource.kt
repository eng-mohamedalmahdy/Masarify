package data.datasource

import domain.Currency

interface CurrencyDatasource {
    fun createCurrency(currency: Currency): Int
    fun updateCurrency(currency: Currency)
    fun deleteCurrency(currency: Currency): Boolean
    fun getAllCurrencies(): List<Currency>
    fun getCurrencyById(id: Int): Currency

}