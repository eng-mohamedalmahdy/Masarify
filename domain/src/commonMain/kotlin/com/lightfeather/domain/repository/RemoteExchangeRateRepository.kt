package com.lightfeather.domain.repository

import com.lightfeather.domain.model.DomainResult

data class RemoteExchangeRate(
    val baseCurrencyCode: String,
    val targetCurrencyCode: String,
    val rate: Double,
)

interface RemoteExchangeRateRepository {
    suspend fun getRemoteRates(): DomainResult<List<RemoteExchangeRate>>
}
