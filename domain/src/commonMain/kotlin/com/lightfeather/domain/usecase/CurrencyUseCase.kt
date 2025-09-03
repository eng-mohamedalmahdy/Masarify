package com.lightfeather.domain.usecase

import com.lightfeather.domain.repository.CurrencyExchangeRateRepository
import com.lightfeather.domain.repository.CurrencyRepository
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.CurrencyExchangeRate
import com.lightfeather.domain.model.DomainResult
import kotlinx.coroutines.flow.first


class CreateCurrency(
    private val currencyRepository: CurrencyRepository,
    private val exchangeRateRepository: CurrencyExchangeRateRepository
) {
    suspend operator fun invoke(currency: Currency): DomainResult<Int> {
        return currencyRepository.createCurrency(currency).mapSuspend { id ->
            currencyRepository.getAllCurrencies().foldSuspend(
                onSuccess = { currencies ->
                    currencies.first().forEach {
                        exchangeRateRepository.createCurrencyExchangeRate(
                            CurrencyExchangeRate(
                                it,
                                currency.copy(id = id),
                                1.0
                            )
                        )
                        exchangeRateRepository.createCurrencyExchangeRate(
                            CurrencyExchangeRate(
                                currency.copy(id = id),
                                it,
                                1.0
                            )
                        )
                    }
                }
            )
            exchangeRateRepository.createCurrencyExchangeRate(
                CurrencyExchangeRate(currency.copy(id = id), currency.copy(id = id), 1.0)
            ).foldResult(
                onSuccess = { id },
                onFailure = { -1 }
            )
        }

    }
}

class UpdateCurrency(private val currencyRepository: CurrencyRepository) {
    suspend operator fun invoke(currency: Currency) = currencyRepository.updateCurrency(currency)
}

class DeleteCurrency(private val currencyRepository: CurrencyRepository) {
    suspend operator fun invoke(currency: Currency) = currencyRepository.deleteCurrency(currency)
}

class GetCurrencyById(private val currencyRepository: CurrencyRepository) {
    suspend operator fun invoke(id: Int) = currencyRepository.getCurrencyById(id)
}


class GetAllCurrencies(private val currencyRepository: CurrencyRepository) {
    suspend operator fun invoke() = currencyRepository.getAllCurrencies()
}





