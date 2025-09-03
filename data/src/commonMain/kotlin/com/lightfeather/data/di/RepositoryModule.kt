package com.lightfeather.data.di

import com.lightfeather.data.repository.AccountRepositoryImpl
import com.lightfeather.data.repository.CategoryRepositoryImpl
import com.lightfeather.data.repository.CurrencyExchangeRateRepositoryImpl
import com.lightfeather.data.repository.CurrencyRepositoryImpl
import com.lightfeather.data.repository.TransactionsRepositoryImpl
import com.lightfeather.data.repository.UserRepositoryImpl
import com.lightfeather.domain.repository.AccountRepository
import com.lightfeather.domain.repository.CategoryRepository
import com.lightfeather.domain.repository.CurrencyExchangeRateRepository
import com.lightfeather.domain.repository.CurrencyRepository
import com.lightfeather.domain.repository.TransactionRepository
import com.lightfeather.domain.repository.UserRepository
import org.koin.dsl.module


val repositoryModule = module {
    single<AccountRepository> { AccountRepositoryImpl(get()) }
    single<TransactionRepository> { TransactionsRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<CurrencyRepository> { CurrencyRepositoryImpl(get()) }
    single<CurrencyExchangeRateRepository> { CurrencyExchangeRateRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }

}