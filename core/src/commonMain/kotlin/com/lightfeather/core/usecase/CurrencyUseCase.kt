package usecase

import data.repository.CurrencyRepository
import domain.Currency

class CreateCurrency(private val currencyRepository: CurrencyRepository) {
    operator fun invoke(currency: Currency) = currencyRepository.createCurrency(currency)
}

class UpdateCurrency(private val currencyRepository: CurrencyRepository) {
    operator fun invoke(currency: Currency) = currencyRepository.updateCurrency(currency)
}

class DeleteCurrency(private val currencyRepository: CurrencyRepository) {
    operator fun invoke(currency: Currency) = currencyRepository.deleteCurrency(currency)
}

class GetCurrencyById(private val currencyRepository: CurrencyRepository) {
    operator fun invoke(id: Int) = currencyRepository.getCurrencyById(id)
}


class GetAllCurrencies(private val currencyRepository: CurrencyRepository) {
    operator fun invoke() = currencyRepository.getAllCurrencies()
}





