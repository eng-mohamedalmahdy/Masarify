package com.lightfeather.core.usecase

import com.lightfeather.core.data.repository.AccountRepository
import com.lightfeather.core.data.repository.CurrencyExchangeRateRepository
import com.lightfeather.core.data.repository.CurrencyRepository
import com.lightfeather.core.domain.WealthWorthInCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

data class GetWealthWorthInCurrency(
    private val accountRepository: AccountRepository,
    private val currencyExchangeRateRepository: CurrencyExchangeRateRepository,
    private val currencyRepository: CurrencyRepository
) {
     operator fun invoke(): Flow<List<WealthWorthInCurrency>> {
        return combine(
            accountRepository.getAccounts(),
            currencyRepository.getAllCurrencies()
        ) { accounts, currencies ->
            currencies.map { targetCurrency ->
                val totalWorth = accounts.sumOf { account ->
                    if (account.currency.id == targetCurrency.id) {
                        account.balance
                    } else {
                        val rate = currencyExchangeRateRepository
                            .getExchangeRatesOfCurrency(account.currency)
                            .firstOrNull()
                            ?.find { it.to.id == targetCurrency.id }
                            ?.rate ?: 1.0
                        account.balance * rate
                    }
                }
                WealthWorthInCurrency(targetCurrency, totalWorth)
            }
        }
    }
}
