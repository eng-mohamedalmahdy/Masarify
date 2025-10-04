package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.CurrencyExchangeRate
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.repository.CurrencyExchangeRateRepository
import com.lightfeather.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.first

class CreateCurrency(
    private val currencyRepository: CurrencyRepository,
    private val exchangeRateRepository: CurrencyExchangeRateRepository,
) {
    suspend operator fun invoke(currency: Currency): DomainResult<Int> =
        currencyRepository.createCurrency(currency).mapSuspend { currencyId ->
            currencyRepository.getAllCurrencies().foldSuspend(
                onSuccess = { currencies ->
                    currencies.first().dropLast(1).forEach {
                        print("Currencies: $it")
                        exchangeRateRepository.createCurrencyExchangeRate(
                            CurrencyExchangeRate(
                                it,
                                currency.copy(id = currencyId),
                                1.0,
                            ),
                        ).fold(
                            onSuccess = { println("Exchange rate created: $it") },
                            onFailure = { println("Exchange rate creation failed: $it") }
                        )
                        exchangeRateRepository.createCurrencyExchangeRate(
                            CurrencyExchangeRate(
                                currency.copy(id = currencyId),
                                it,
                                1.0,
                            ),
                        ).fold(
                            onSuccess = { println("Exchange rate rev created: $it") },
                            onFailure = { println("Exchange rate rev creation failed: $it") }
                        )
                    }
                },
            )
            exchangeRateRepository
                .createCurrencyExchangeRate(
                    CurrencyExchangeRate(currency.copy(id = currencyId), currency.copy(id = currencyId), 1.0),
                ).foldResult(
                    onSuccess = { currencyId },
                    onFailure = { -1 },
                )
        }
}

class UpdateCurrency(
    private val currencyRepository: CurrencyRepository,
) {
    suspend operator fun invoke(currency: Currency) = currencyRepository.updateCurrency(currency)
}

class DeleteCurrency(
    private val currencyRepository: CurrencyRepository,
) {
    suspend operator fun invoke(currency: Currency) = currencyRepository.deleteCurrency(currency)
}

class GetCurrencyById(
    private val currencyRepository: CurrencyRepository,
) {
    suspend operator fun invoke(id: Int) = currencyRepository.getCurrencyById(id)
}

class GetAllCurrencies(
    private val currencyRepository: CurrencyRepository,
) {
    operator fun invoke() = currencyRepository.getAllCurrencies()
}
