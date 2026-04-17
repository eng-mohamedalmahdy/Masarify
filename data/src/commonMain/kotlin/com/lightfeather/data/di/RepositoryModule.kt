package com.lightfeather.data.di

import com.lightfeather.data.repository.AccountRepositoryImpl
import com.lightfeather.data.repository.AttachmentRepositoryImpl
import com.lightfeather.data.repository.BackupRepositoryImpl
import com.lightfeather.data.repository.BankNameRepositoryImpl
import com.lightfeather.data.repository.CategoryRepositoryImpl
import com.lightfeather.data.repository.CurrencyExchangeRateRepositoryImpl
import com.lightfeather.data.repository.CurrencyRepositoryImpl
import com.lightfeather.data.repository.FinancialSessionRepositoryImpl
import com.lightfeather.data.repository.RemoteExchangeRateRepositoryImpl
import com.lightfeather.data.repository.TransactionsRepositoryImpl
import com.lightfeather.data.repository.UserRepositoryImpl
import com.lightfeather.domain.repository.AccountRepository
import com.lightfeather.domain.repository.AttachmentRepository
import com.lightfeather.domain.repository.BackupRepository
import com.lightfeather.domain.repository.BankNameRepository
import com.lightfeather.domain.repository.CategoryRepository
import com.lightfeather.domain.repository.CurrencyExchangeRateRepository
import com.lightfeather.domain.repository.CurrencyRepository
import com.lightfeather.domain.repository.FinancialSessionRepository
import com.lightfeather.domain.repository.RemoteExchangeRateRepository
import com.lightfeather.domain.repository.TransactionRepository
import com.lightfeather.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModule =
    module {
        single<AccountRepository> { AccountRepositoryImpl(get()) }
        single<AttachmentRepository> { AttachmentRepositoryImpl(get()) }
        single<TransactionRepository> { TransactionsRepositoryImpl(get(), get()) }
        single<CategoryRepository> { CategoryRepositoryImpl(get(), get()) }
        single<CurrencyRepository> { CurrencyRepositoryImpl(get()) }
        single<CurrencyExchangeRateRepository> { CurrencyExchangeRateRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
        single<BankNameRepository> { BankNameRepositoryImpl(get()) }
        single<FinancialSessionRepository> { FinancialSessionRepositoryImpl(get()) }
        single<RemoteExchangeRateRepository> { RemoteExchangeRateRepositoryImpl(get()) }
        single<BackupRepository> { BackupRepositoryImpl(get(), get()) }
    }
