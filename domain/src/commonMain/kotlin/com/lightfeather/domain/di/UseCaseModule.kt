package com.lightfeather.domain.di

import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.usecase.CreateAccount
import com.lightfeather.domain.usecase.CreateCategory
import com.lightfeather.domain.usecase.CreateCurrency
import com.lightfeather.domain.usecase.CreateTransaction
import com.lightfeather.domain.usecase.DeleteAccount
import com.lightfeather.domain.usecase.DeleteCategory
import com.lightfeather.domain.usecase.DeleteCurrency
import com.lightfeather.domain.usecase.DeleteTransaction
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.domain.usecase.GetAllCategories
import com.lightfeather.domain.usecase.GetAllCategoryIcons
import com.lightfeather.domain.usecase.GetAllCurrencies
import com.lightfeather.domain.usecase.GetAllCurrenciesExchangeRates
import com.lightfeather.domain.usecase.GetAllTransactions
import com.lightfeather.domain.usecase.GetAllTransactionsOfTypePaged
import com.lightfeather.domain.usecase.GetAllTransactionsPaged
import com.lightfeather.domain.usecase.GetExchangeRatesOfCurrency
import com.lightfeather.domain.usecase.GetFilteredTransactionCount
import com.lightfeather.domain.usecase.GetFilteredTransactions
import com.lightfeather.domain.usecase.GetFilteredTransactionsPaged
import com.lightfeather.domain.usecase.GetTotalExpenseOfCurrency
import com.lightfeather.domain.usecase.GetTotalIncomeOfCurrency
import com.lightfeather.domain.usecase.GetTotalTransactionsByCategories
import com.lightfeather.domain.usecase.GetTransactionCount
import com.lightfeather.domain.usecase.GetTransactionCountOfType
import com.lightfeather.domain.usecase.GetUsedCurrencies
import com.lightfeather.domain.usecase.GetUserDarkMode
import com.lightfeather.domain.usecase.GetUserLanguage
import com.lightfeather.domain.usecase.GetUserSavedColors
import com.lightfeather.domain.usecase.GetWealthWorthInCurrency
import com.lightfeather.domain.usecase.SaveUserColor
import com.lightfeather.domain.usecase.SeedApplicationData
import com.lightfeather.domain.usecase.SeedDefaultBankNames
import com.lightfeather.domain.usecase.SeedDefaultCategories
import com.lightfeather.domain.usecase.SeedDefaultCurrencies
import com.lightfeather.domain.usecase.SetLanguage
import com.lightfeather.domain.usecase.ToggleDarkMode
import com.lightfeather.domain.usecase.UpdateAccount
import com.lightfeather.domain.usecase.UpdateCategory
import com.lightfeather.domain.usecase.UpdateCurrency
import com.lightfeather.domain.usecase.UpdateCurrencyExchangeRates
import com.lightfeather.domain.usecase.UpdateTransaction
import com.lightfeather.domain.usecase.UpsertUserData
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule =
    module {

        factory { UpsertUserData(get()) }
        factory { GetAllTransactions(get()) }
        factory { GetAllTransactions(get()) }
        factory { GetAllCategories(get()) }
        factory { GetAllAccounts(get()) }

        factory { DeleteAccount(get()) }
        factory { UpdateAccount(get()) }
        factory<CreateTransaction> {
            CreateTransaction(get())
        }

        factory<DeleteTransaction> {
            DeleteTransaction(get())
        }

        factory<UpdateTransaction> {
            UpdateTransaction(get())
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
        factory { CreateCategory(get()) }
        factory { SeedDefaultCategories(get()) }
        factory { SeedDefaultCurrencies(get()) }
        factory { SeedDefaultBankNames(get()) }
        factory { SeedApplicationData(get(), get(), get(), get()) }
        factory { CreateAccount(get()) }
        factory { GetAllCurrencies(get()) }
        factory { GetAllCurrenciesExchangeRates(get()) }
        factory { CreateCurrency(get(), get()) }
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

        factory { SetLanguage(get()) }
        factory { GetUserLanguage(get()) }

        factory { ToggleDarkMode(get()) }

        factory { GetUserDarkMode(get()) }

        factory { DeleteCategory(get()) }
        factory { UpdateCategory(get()) }
        factory { UpdateCurrency(get()) }
        factory { DeleteCurrency(get()) }
        factory { GetExchangeRatesOfCurrency(get()) }
        factoryOf(::GetUsedCurrencies)
        factoryOf(::GetFilteredTransactions)
    }
