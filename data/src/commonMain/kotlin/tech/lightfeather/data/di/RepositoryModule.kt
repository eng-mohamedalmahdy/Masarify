package tech.lightfeather.data.di

import org.koin.dsl.module
import tech.lightfeather.data.repository.AccountRepositoryImpl
import tech.lightfeather.data.repository.AttachmentRepositoryImpl
import tech.lightfeather.data.repository.AuthRepositoryImpl
import tech.lightfeather.data.repository.BackupRepositoryImpl
import tech.lightfeather.data.repository.BankNameRepositoryImpl
import tech.lightfeather.data.repository.CategoryRepositoryImpl
import tech.lightfeather.data.repository.CurrencyExchangeRateRepositoryImpl
import tech.lightfeather.data.repository.CurrencyRepositoryImpl
import tech.lightfeather.data.repository.DeviceTokenRepositoryImpl
import tech.lightfeather.data.repository.FinancialSessionRepositoryImpl
import tech.lightfeather.data.repository.RemoteExchangeRateRepositoryImpl
import tech.lightfeather.data.repository.SyncQueueRepositoryImpl
import tech.lightfeather.data.repository.SyncRepositoryImpl
import tech.lightfeather.data.repository.TransactionsRepositoryImpl
import tech.lightfeather.data.repository.NotificationRepositoryImpl
import tech.lightfeather.data.repository.UserRepositoryImpl
import tech.lightfeather.domain.repository.AccountRepository
import tech.lightfeather.domain.repository.AttachmentRepository
import tech.lightfeather.domain.repository.AuthRepository
import tech.lightfeather.domain.repository.BackupRepository
import tech.lightfeather.domain.repository.BankNameRepository
import tech.lightfeather.domain.repository.CategoryRepository
import tech.lightfeather.domain.repository.CurrencyExchangeRateRepository
import tech.lightfeather.domain.repository.CurrencyRepository
import tech.lightfeather.domain.repository.DeviceTokenRepository
import tech.lightfeather.domain.repository.FinancialSessionRepository
import tech.lightfeather.domain.repository.RemoteExchangeRateRepository
import tech.lightfeather.domain.repository.SyncQueueRepository
import tech.lightfeather.domain.repository.SyncRepository
import tech.lightfeather.domain.repository.TransactionRepository
import tech.lightfeather.domain.repository.NotificationRepository
import tech.lightfeather.domain.repository.UserRepository

val repositoryModule =
    module {
        single<SyncQueueRepository> { SyncQueueRepositoryImpl(get()) }
        single<AccountRepository> { AccountRepositoryImpl(get()) }
        single<AttachmentRepository> { AttachmentRepositoryImpl(get()) }
        single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
        single<TransactionRepository> { TransactionsRepositoryImpl(get(), get()) }
        single<CategoryRepository> { CategoryRepositoryImpl(get(), get()) }
        single<CurrencyRepository> { CurrencyRepositoryImpl(get()) }
        single<CurrencyExchangeRateRepository> { CurrencyExchangeRateRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
        single<BankNameRepository> { BankNameRepositoryImpl(get()) }
        single<FinancialSessionRepository> { FinancialSessionRepositoryImpl(get()) }
        single<RemoteExchangeRateRepository> { RemoteExchangeRateRepositoryImpl(get()) }
        single<BackupRepository> { BackupRepositoryImpl(get(), get()) }
        single<SyncRepository> { SyncRepositoryImpl(get()) }
        single<DeviceTokenRepository> { DeviceTokenRepositoryImpl(get()) }
        single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    }
