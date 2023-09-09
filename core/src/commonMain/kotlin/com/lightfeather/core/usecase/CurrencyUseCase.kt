package com.lightfeather.core.usecase

import com.lightfeather.core.data.repository.CurrencyRepository
import com.lightfeather.core.domain.Currency

class CreateCurrency(private val currencyRepository: CurrencyRepository) {
   suspend operator fun invoke(currency: Currency) = currencyRepository.createCurrency(currency)
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





