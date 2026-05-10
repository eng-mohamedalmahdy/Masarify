package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.first
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.CurrencyExchangeRate
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.CurrencyExchangeRateRepository
import tech.lightfeather.domain.repository.CurrencyRepository

class CreateCurrency(
    private val currencyRepository: CurrencyRepository,
    private val exchangeRateRepository: CurrencyExchangeRateRepository,
    private val syncHelper: SyncEnqueueHelper,
) {
    @Suppress("LongMethod")
    suspend operator fun invoke(currency: Currency): DomainResult<Int> {
        val createResult = currencyRepository.createCurrency(currency)
        createResult.getOrNull()?.let { newId ->
            syncHelper.enqueue("CURRENCY", "CREATE", currency.copy(id = newId), Currency.serializer(), newId)
        }
        return createResult.mapSuspend { currencyId ->
            currencyRepository.getAllCurrencies().foldSuspend(
                onSuccess = { currencies ->
                    currencies.first().dropLast(1).forEach {
                        print("Currencies: $it")
                        exchangeRateRepository
                            .createCurrencyExchangeRate(
                                CurrencyExchangeRate(
                                    it,
                                    currency.copy(id = currencyId),
                                    1.0,
                                ),
                            ).fold(
                                onSuccess = { println("Exchange rate created: $it") },
                                onFailure = { println("Exchange rate creation failed: $it") },
                            )
                        exchangeRateRepository
                            .createCurrencyExchangeRate(
                                CurrencyExchangeRate(
                                    currency.copy(id = currencyId),
                                    it,
                                    1.0,
                                ),
                            ).fold(
                                onSuccess = { println("Exchange rate rev created: $it") },
                                onFailure = { println("Exchange rate rev creation failed: $it") },
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
}

class UpdateCurrency(
    private val currencyRepository: CurrencyRepository,
    private val syncHelper: SyncEnqueueHelper,
) {
    suspend operator fun invoke(currency: Currency) =
        currencyRepository.updateCurrency(currency).also { result ->
            if (result.isSuccess) {
                syncHelper.enqueue("CURRENCY", "UPDATE", currency, Currency.serializer(), currency.id)
            }
        }
}

class DeleteCurrency(
    private val currencyRepository: CurrencyRepository,
    private val syncHelper: SyncEnqueueHelper,
) {
    suspend operator fun invoke(currency: Currency) =
        currencyRepository.deleteCurrency(currency).also { result ->
            if (result.isSuccess) {
                syncHelper.enqueue("CURRENCY", "DELETE", currency, Currency.serializer(), currency.id)
            }
        }
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

class GetUsedCurrencies(
    private val currencyRepository: CurrencyRepository,
) {
    suspend operator fun invoke() = currencyRepository.getUsedCurrencies()
}
