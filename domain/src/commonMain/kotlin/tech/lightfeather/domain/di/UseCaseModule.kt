package tech.lightfeather.domain.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import tech.lightfeather.domain.model.transaction.Transaction
import tech.lightfeather.domain.repository.getPlatform
import tech.lightfeather.domain.usecase.AcquireAndUpdateFcmUseCase
import tech.lightfeather.domain.usecase.BankNameUseCase
import tech.lightfeather.domain.usecase.ClearLocalDataUseCase
import tech.lightfeather.domain.usecase.CreateAccount
import tech.lightfeather.domain.usecase.CreateCategory
import tech.lightfeather.domain.usecase.CreateCurrency
import tech.lightfeather.domain.usecase.CreateFinancialSession
import tech.lightfeather.domain.usecase.CreateTransaction
import tech.lightfeather.domain.usecase.DeleteAccount
import tech.lightfeather.domain.usecase.DeleteAllFailedSyncUseCase
import tech.lightfeather.domain.usecase.DeleteCategory
import tech.lightfeather.domain.usecase.DeleteCurrency
import tech.lightfeather.domain.usecase.DeleteFailedSyncEntryUseCase
import tech.lightfeather.domain.usecase.DeleteFinancialSession
import tech.lightfeather.domain.usecase.DeleteTransaction
import tech.lightfeather.domain.usecase.DismissNotificationBanner
import tech.lightfeather.domain.usecase.DismissNotificationTip
import tech.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import tech.lightfeather.domain.usecase.ExportDataUseCase
import tech.lightfeather.domain.usecase.ForgotPasswordUseCase
import tech.lightfeather.domain.usecase.GetActiveTip
import tech.lightfeather.domain.usecase.GetAllAccounts
import tech.lightfeather.domain.usecase.GetAllCategories
import tech.lightfeather.domain.usecase.GetAllCategoryIcons
import tech.lightfeather.domain.usecase.GetAllCurrencies
import tech.lightfeather.domain.usecase.GetAllCurrenciesExchangeRates
import tech.lightfeather.domain.usecase.GetAllFinancialSessions
import tech.lightfeather.domain.usecase.GetAllTransactions
import tech.lightfeather.domain.usecase.GetAllTransactionsOfTypePaged
import tech.lightfeather.domain.usecase.GetAllTransactionsPaged
import tech.lightfeather.domain.usecase.GetDefaultAccount
import tech.lightfeather.domain.usecase.GetExchangeRatesOfCurrency
import tech.lightfeather.domain.usecase.GetExpenseCategoriesByUsage
import tech.lightfeather.domain.usecase.GetFailedSyncCountUseCase
import tech.lightfeather.domain.usecase.GetFailedSyncEntriesUseCase
import tech.lightfeather.domain.usecase.GetFilteredTransactionCount
import tech.lightfeather.domain.usecase.GetFilteredTransactions
import tech.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import tech.lightfeather.domain.usecase.GetFinancialSessionById
import tech.lightfeather.domain.usecase.GetNotificationSettings
import tech.lightfeather.domain.usecase.GetSubscriptionStatusUseCase
import tech.lightfeather.domain.usecase.GetTotalExpenseOfCurrency
import tech.lightfeather.domain.usecase.GetTotalIncomeOfCurrency
import tech.lightfeather.domain.usecase.GetTotalTransactionsByCategories
import tech.lightfeather.domain.usecase.GetTransactionCount
import tech.lightfeather.domain.usecase.GetTransactionCountOfType
import tech.lightfeather.domain.usecase.GetUsedCurrencies
import tech.lightfeather.domain.usecase.GetUserDarkMode
import tech.lightfeather.domain.usecase.GetUserLanguage
import tech.lightfeather.domain.usecase.GetUserSavedColors
import tech.lightfeather.domain.usecase.GetWealthWorthInCurrency
import tech.lightfeather.domain.usecase.ImportDataUseCase
import tech.lightfeather.domain.usecase.InitializeRevenueCatUseCase
import tech.lightfeather.domain.usecase.IsAuthenticatedUseCase
import tech.lightfeather.domain.usecase.IsEmailVerifiedUseCase
import tech.lightfeather.domain.usecase.IsOnboardingComplete
import tech.lightfeather.domain.usecase.IsProActiveUseCase
import tech.lightfeather.domain.usecase.LinkRevenueCatCustomerUseCase
import tech.lightfeather.domain.usecase.LoginUseCase
import tech.lightfeather.domain.usecase.LogoutAllDevicesUseCase
import tech.lightfeather.domain.usecase.LogoutUseCase
import tech.lightfeather.domain.usecase.MarkOnboardingComplete
import tech.lightfeather.domain.usecase.PullRemoteDeltaUseCase
import tech.lightfeather.domain.usecase.PurchaseProUseCase
import tech.lightfeather.domain.usecase.RecordAppOpen
import tech.lightfeather.domain.usecase.RecordReminderFired
import tech.lightfeather.domain.usecase.RegisterDeviceTokenUseCase
import tech.lightfeather.domain.usecase.RegisterUseCase
import tech.lightfeather.domain.usecase.ResendVerificationUseCase
import tech.lightfeather.domain.usecase.ResetPasswordUseCase
import tech.lightfeather.domain.usecase.RestorePurchasesUseCase
import tech.lightfeather.domain.usecase.RetryAllFailedSyncUseCase
import tech.lightfeather.domain.usecase.RetrySyncEntryUseCase
import tech.lightfeather.domain.usecase.SaveUserColor
import tech.lightfeather.domain.usecase.SeedApplicationData
import tech.lightfeather.domain.usecase.SeedDefaultBankNames
import tech.lightfeather.domain.usecase.SeedDefaultCategories
import tech.lightfeather.domain.usecase.SeedDefaultCurrencies
import tech.lightfeather.domain.usecase.SetDefaultAccount
import tech.lightfeather.domain.usecase.SetLanguage
import tech.lightfeather.domain.usecase.ShouldFireReminder
import tech.lightfeather.domain.usecase.ShouldShowNotificationBanner
import tech.lightfeather.domain.usecase.SyncEnqueueHelper
import tech.lightfeather.domain.usecase.SyncRemoteExchangeRatesUseCase
import tech.lightfeather.domain.usecase.ToggleDarkMode
import tech.lightfeather.domain.usecase.UpdateAccount
import tech.lightfeather.domain.usecase.UpdateCategory
import tech.lightfeather.domain.usecase.UpdateCurrency
import tech.lightfeather.domain.usecase.UpdateCurrencyExchangeRates
import tech.lightfeather.domain.usecase.UpdateFinancialSession
import tech.lightfeather.domain.usecase.UpdateNotificationSettings
import tech.lightfeather.domain.usecase.UpdateTransaction
import tech.lightfeather.domain.usecase.UploadLocalDataUseCase
import tech.lightfeather.domain.usecase.UpsertUserData
import tech.lightfeather.domain.usecase.VerifyEmailUseCase

val useCaseModule =
    module {

        single { SyncEnqueueHelper(get(), get(), get(), get(), get()) }

        factory { UpsertUserData(get()) }
        factory { GetAllTransactions(get()) }
        factory { GetAllTransactions(get()) }
        factory { GetAllCategories(get()) }
        factory { GetAllAccounts(get()) }

        factory { DeleteAccount(get(), get(), get()) }
        factory { UpdateAccount(get(), get(), get()) }
        factory<CreateTransaction> {
            CreateTransaction(get(), get(), get(), get())
        }

        factory<DeleteTransaction> {
            DeleteTransaction(get(), get(), get(), get())
        }

        factory<UpdateTransaction> {
            UpdateTransaction(get(), get(), get(), get())
        }

        factory<GetTotalTransactionsByCategories<Transaction.Expense>>(named("expense")) {
            GetTotalTransactionsByCategories(get())
        }

        factory<GetTotalTransactionsByCategories<Transaction.Income>>(named("income")) {
            GetTotalTransactionsByCategories(get())
        }

        factory<GetTotalTransactionsByCategories<Transaction.Transfer>>(named("transfer")) {
            GetTotalTransactionsByCategories(get())
        }

        factory { GetAllCategoryIcons(get(), get()) }
        factory { CreateCategory(get(), get()) }
        factory { SeedDefaultCategories(get()) }
        factory { SeedDefaultCurrencies(get()) }
        factory { SeedDefaultBankNames(get()) }
        factory { SeedApplicationData(get(), get(), get(), get()) }
        factory { CreateAccount(get(), get(), get()) }
        factory { SetDefaultAccount(get()) }
        factory { GetDefaultAccount(get()) }
        factory { BankNameUseCase.GetAllBankNames(get()) }
        factory { BankNameUseCase.CreateBankName(get()) }
        factory { GetAllCurrencies(get()) }
        factory { GetAllCurrenciesExchangeRates(get()) }
        factory { CreateCurrency(get(), get(), get()) }
        factory { UpdateCurrencyExchangeRates(get()) }
        factory { GetTotalExpenseOfCurrency(get()) }
        factory { GetTotalIncomeOfCurrency(get()) }
        factory { GetWealthWorthInCurrency(get(), get(), get()) }

        // Pagination use cases
        factory { GetAllTransactionsPaged(get()) }
        factory { GetFilteredTransactionsPaged(get()) }
        factory { GetTransactionCount(get()) }
        factory { GetFilteredTransactionCount(get()) }

        // Type-specific pagination use cases
        factory<GetAllTransactionsOfTypePaged<Transaction.Expense>>(named("expense-paged")) {
            GetAllTransactionsOfTypePaged(get())
        }
        factory<GetAllTransactionsOfTypePaged<Transaction.Income>>(named("income-paged")) {
            GetAllTransactionsOfTypePaged(get())
        }
        factory<GetAllTransactionsOfTypePaged<Transaction.Transfer>>(named("transfer-paged")) {
            GetAllTransactionsOfTypePaged(get())
        }

        factory<GetTransactionCountOfType<Transaction.Expense>>(named("expense-count")) {
            GetTransactionCountOfType(get())
        }
        factory<GetTransactionCountOfType<Transaction.Income>>(named("income-count")) {
            GetTransactionCountOfType(get())
        }
        factory<GetTransactionCountOfType<Transaction.Transfer>>(named("transfer-count")) {
            GetTransactionCountOfType(get())
        }

        factory { GetUserSavedColors(get()) }

        factory { SaveUserColor(get()) }

        factoryOf(::IsOnboardingComplete)
        factoryOf(::MarkOnboardingComplete)

        factory { SetLanguage(get()) }
        factory { GetUserLanguage(get()) }

        factory { ToggleDarkMode(get()) }

        factory { GetUserDarkMode(get()) }

        factory { DeleteCategory(get(), get()) }
        factory { UpdateCategory(get(), get()) }
        factory { UpdateCurrency(get(), get()) }
        factory { DeleteCurrency(get(), get()) }
        factory { GetExchangeRatesOfCurrency(get()) }
        factoryOf(::GetUsedCurrencies)
        factoryOf(::GetFilteredTransactions)

        // Financial Session use cases
        factory { CreateFinancialSession(get(), get(), get()) }
        factory { UpdateFinancialSession(get(), get()) }
        factory { DeleteFinancialSession(get(), get()) }
        factoryOf(::GetAllFinancialSessions)
        factoryOf(::GetFinancialSessionById)
        factoryOf(::GetExpenseCategoriesByUsage)
        factoryOf(::SyncRemoteExchangeRatesUseCase)
        factoryOf(::ExportDataUseCase)
        factoryOf(::ImportDataUseCase)

        // Auth use cases
        factoryOf(::LoginUseCase)
        factoryOf(::RegisterUseCase)
        factoryOf(::ClearLocalDataUseCase)
        factoryOf(::LogoutUseCase)
        factoryOf(::LogoutAllDevicesUseCase)
        factoryOf(::IsAuthenticatedUseCase)
        factoryOf(::IsEmailVerifiedUseCase)
        factoryOf(::VerifyEmailUseCase)
        factoryOf(::ResendVerificationUseCase)
        factoryOf(::ForgotPasswordUseCase)
        factoryOf(::ResetPasswordUseCase)

        factory { AcquireAndUpdateFcmUseCase(getPlatform(), get(), get(), get()) }

        // Sync use cases
        factoryOf(::DrainOutboxQueueUseCase)
        factoryOf(::PullRemoteDeltaUseCase)
        factoryOf(::UploadLocalDataUseCase)
        factoryOf(::RegisterDeviceTokenUseCase)

        // Notification use cases
        factoryOf(::GetActiveTip)
        factoryOf(::DismissNotificationTip)
        factoryOf(::ShouldShowNotificationBanner)
        factoryOf(::DismissNotificationBanner)
        factoryOf(::GetNotificationSettings)
        factoryOf(::UpdateNotificationSettings)
        factoryOf(::RecordAppOpen)
        factoryOf(::RecordReminderFired)
        factoryOf(::ShouldFireReminder)

        // Subscription use cases
        factoryOf(::GetSubscriptionStatusUseCase)
        factoryOf(::IsProActiveUseCase)
        factoryOf(::PurchaseProUseCase)
        factoryOf(::RestorePurchasesUseCase)
        factoryOf(::LinkRevenueCatCustomerUseCase)
        factoryOf(::InitializeRevenueCatUseCase)

        // Sync queue use cases
        factoryOf(::GetFailedSyncEntriesUseCase)
        factoryOf(::GetFailedSyncCountUseCase)
        factoryOf(::RetrySyncEntryUseCase)
        factoryOf(::RetryAllFailedSyncUseCase)
        factoryOf(::DeleteFailedSyncEntryUseCase)
        factoryOf(::DeleteAllFailedSyncUseCase)
    }
