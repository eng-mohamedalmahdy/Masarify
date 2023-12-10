package com.lightfeather.core.data.repository

import com.lightfeather.core.data.datasource.CurrencyDatasource
import com.lightfeather.core.domain.Currency

class CurrencyRepository(private val datasource: CurrencyDatasource) {
    suspend fun createCurrency(currency: Currency): Int = datasource.createCurrency(currency)

    suspend fun updateCurrency(currency: Currency) = datasource.updateCurrency(currency)

    suspend fun deleteCurrency(currency: Currency): Boolean = datasource.deleteCurrency(currency)

    suspend fun getAllCurrencies() = datasource.getAllCurrencies()

    suspend fun getCurrencyById(id: Int): Currency? = datasource.getCurrencyById(id)
}