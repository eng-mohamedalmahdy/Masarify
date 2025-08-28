package com.lightfeather.data.di

import com.lightfeather.data.repository.AccountRepositoryImpl
import org.koin.dsl.module


val repositoryModule = module {
    single { AccountRepositoryImpl(get()) }
//    single { ExpensesRepository(get()) }
//    single { TransferRepository(get()) }
//    single { IncomeRepository(get()) }
//    single { CategoryRepository(get()) }
//    single { CurrencyRepository(get()) }
//    single { CurrencyExchangeRateRepository(get()) }
}