package com.lightfeather.core.data.datasource

import com.lightfeather.core.domain.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyDatasource {
    suspend fun createCurrency(currency: Currency): Int
    suspend fun updateCurrency(currency: Currency)
    suspend fun deleteCurrency(currency: Currency): Boolean
    fun getAllCurrencies(): Flow<List<Currency>>
    suspend fun getCurrencyById(id: Int): Currency?

}