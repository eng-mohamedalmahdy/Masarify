package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.WealthWorthInCurrency
import com.lightfeather.domain.repository.AccountRepository
import com.lightfeather.domain.repository.CurrencyExchangeRateRepository
import com.lightfeather.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

class GetWealthWorthInCurrency(
    private val accountRepository: AccountRepository,
    private val currencyExchangeRateRepository: CurrencyExchangeRateRepository,
    private val currencyRepository: CurrencyRepository,
) {
    suspend operator fun invoke(): DomainResult<Flow<List<WealthWorthInCurrency>>> =
        DomainResult.combine(
            accountRepository.getAccounts(),
            currencyRepository.getUsedCurrencies(),
        ) { accountsFlow, currenciesFlow ->
            combine(accountsFlow, currenciesFlow) { accounts, currencies ->
                currencies.map { targetCurrency ->
                    val totalWorth =
                        accounts.sumOf { account ->
                            if (account.currency.id == targetCurrency.id) {
                                account.balance
                            } else {
                                val rate =
                                    currencyExchangeRateRepository
                                        .getExchangeRatesOfCurrency(account.currency)
                                        .getOrNull() // still a DomainResult<Flow<…>>
                                        ?.firstOrNull()
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
