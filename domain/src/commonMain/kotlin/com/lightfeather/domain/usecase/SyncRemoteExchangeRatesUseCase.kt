package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.CurrencyExchangeRate
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.repository.CurrencyExchangeRateRepository
import com.lightfeather.domain.repository.CurrencyRepository
import com.lightfeather.domain.repository.RemoteExchangeRateRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first

class SyncRemoteExchangeRatesUseCase(
    private val remoteRepository: RemoteExchangeRateRepository,
    private val currencyRepository: CurrencyRepository,
    private val exchangeRateRepository: CurrencyExchangeRateRepository,
) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(): DomainResult<Unit> =
        remoteRepository.getRemoteRates().flatMapSuspend { remoteRates ->
            currencyRepository.getAllCurrencies().flatMapSuspend { currenciesFlow ->
                try {
                    val currencies = currenciesFlow.first()
                    val isoCodeMap =
                        currencies
                            .filter { it.isoCode != null }
                            .associateBy { it.isoCode!! }

                    val rates =
                        remoteRates.mapNotNull { remote ->
                            val from = isoCodeMap[remote.baseCurrencyCode] ?: return@mapNotNull null
                            val to = isoCodeMap[remote.targetCurrencyCode] ?: return@mapNotNull null
                            CurrencyExchangeRate(from = from, to = to, rate = remote.rate)
                        }

                    if (rates.isEmpty()) {
                        Napier.d("No matching currencies for remote rates", tag = "SyncRates")
                    } else {
                        rates.forEach { rate ->
                            exchangeRateRepository.createCurrencyExchangeRate(rate).fold(
                                onFailure = {
                                    Napier.w(
                                        "Failed to upsert rate ${rate.from.isoCode}->${rate.to.isoCode}",
                                        tag = "SyncRates",
                                    )
                                },
                            )
                        }
                    }
                    DomainResult.Success(Unit)
                } catch (e: Exception) {
                    Napier.e("Error syncing remote rates", e, tag = "SyncRates")
                    DomainResult.Success(Unit) // best-effort: don't fail the app
                }
            }
        }
}
