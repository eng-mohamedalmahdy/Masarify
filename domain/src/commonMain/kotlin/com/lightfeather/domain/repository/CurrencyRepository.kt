package com.lightfeather.domain.repository

import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.DomainResult
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    suspend fun createCurrency(currency: Currency): DomainResult<Int>

    suspend fun updateCurrency(currency: Currency): DomainResult<Boolean>

    suspend fun deleteCurrency(currency: Currency): DomainResult<Boolean>

    suspend fun getCurrencyById(id: Int): DomainResult<Currency>

    fun getAllCurrencies(): DomainResult<Flow<List<Currency>>>
}
