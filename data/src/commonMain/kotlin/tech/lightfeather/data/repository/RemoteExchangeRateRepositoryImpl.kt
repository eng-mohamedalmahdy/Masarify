package tech.lightfeather.data.repository

import tech.lightfeather.data.remote.RemoteRatesApi
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.RemoteExchangeRate
import tech.lightfeather.domain.repository.RemoteExchangeRateRepository

class RemoteExchangeRateRepositoryImpl(
    private val api: RemoteRatesApi,
) : RemoteExchangeRateRepository {
    override suspend fun getRemoteRates(): DomainResult<List<RemoteExchangeRate>> =
        api.getRemoteRates().map { dtos ->
            dtos.map { dto ->
                RemoteExchangeRate(
                    baseCurrencyCode = dto.baseCurrencyCode,
                    targetCurrencyCode = dto.targetCurrencyCode,
                    rate = dto.rate,
                )
            }
        }
}
