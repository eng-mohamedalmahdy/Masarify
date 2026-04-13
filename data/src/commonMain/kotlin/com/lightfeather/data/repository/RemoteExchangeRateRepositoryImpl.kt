package com.lightfeather.data.repository

import com.lightfeather.data.remote.RemoteRatesApi
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.repository.RemoteExchangeRate
import com.lightfeather.domain.repository.RemoteExchangeRateRepository

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
