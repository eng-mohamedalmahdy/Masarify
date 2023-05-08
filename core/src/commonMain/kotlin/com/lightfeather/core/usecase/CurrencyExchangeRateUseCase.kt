package usecase

import data.repository.CurrencyExchangeRateRepository
import domain.Currency
import domain.CurrencyExchangeRate


class CreateCurrencyExchangeRate(private val repository: CurrencyExchangeRateRepository) {
    operator fun invoke(currencyExchangeRate: CurrencyExchangeRate) =
        repository.createCurrencyExchangeRate(currencyExchangeRate)
}

class UpdateCurrencyExchangeRate(private val repository: CurrencyExchangeRateRepository) {
    operator fun invoke(currencyExchangeRate: CurrencyExchangeRate) =
        repository.updateCurrencyExchangeRate(currencyExchangeRate)
}

class DeleteCurrencyExchangeRate(private val repository: CurrencyExchangeRateRepository) {
    operator fun invoke(currencyExchangeRate: CurrencyExchangeRate) =
        repository.deleteCurrencyExchangeRate(currencyExchangeRate)
}

class GetAllCurrenciesExchangeRates(private val repository: CurrencyExchangeRateRepository) {
    operator fun invoke() =
        repository.getAllCurrenciesExchangeRates()
}

class GetCurrencyExchangeRateById(private val repository: CurrencyExchangeRateRepository) {
    operator fun invoke(id: Int) =
        repository.getCurrencyExchangeRateById(id)
}

class GetExchangeRatesOfCurrency(private val repository: CurrencyExchangeRateRepository) {
    operator fun invoke(currency: Currency) =
        repository.getExchangeRatesOfCurrency(currency)
}












