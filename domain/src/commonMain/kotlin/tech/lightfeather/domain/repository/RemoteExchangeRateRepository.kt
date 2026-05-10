package tech.lightfeather.domain.repository

import tech.lightfeather.domain.model.DomainResult

data class RemoteExchangeRate(
    val baseCurrencyCode: String,
    val targetCurrencyCode: String,
    val rate: Double,
)

interface RemoteExchangeRateRepository {
    suspend fun getRemoteRates(): DomainResult<List<RemoteExchangeRate>>
}
