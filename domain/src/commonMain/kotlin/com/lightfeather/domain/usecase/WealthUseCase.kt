package com.lightfeather.domain.usecase

import com.lightfeather.domain.data.repository.AccountRepository
import com.lightfeather.domain.data.repository.CurrencyExchangeRateRepository
import com.lightfeather.domain.data.repository.CurrencyRepository
import com.lightfeather.domain.domain.DomainResult
import com.lightfeather.domain.domain.WealthWorthInCurrency
import com.lightfeather.domain.domain.error.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


 class GetWealthWorthInCurrency(
    private val accountRepository: AccountRepository,
    private val currencyExchangeRateRepository: CurrencyExchangeRateRepository,
    private val currencyRepository: CurrencyRepository
) {
     operator fun invoke(): DomainResult<Flow<List<WealthWorthInCurrency>>> {
         return DomainResult.combine(
             accountRepository.getAccounts(),
             currencyRepository.getAllCurrencies()
         ) { accountsFlow, currenciesFlow ->
             combine(accountsFlow, currenciesFlow) { accounts, currencies ->
                 currencies.map { targetCurrency ->
                     val totalWorth = accounts.sumOf { account ->
                         if (account.currency.id == targetCurrency.id) {
                             account.balance
                         } else {
                             val rate = currencyExchangeRateRepository
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
 }
