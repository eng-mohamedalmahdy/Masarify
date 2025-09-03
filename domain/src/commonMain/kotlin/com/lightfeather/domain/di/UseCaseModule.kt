package com.lightfeather.domain.di

import com.lightfeather.domain.usecase.CreateTransaction
import com.lightfeather.domain.usecase.DeleteTransaction
import com.lightfeather.domain.usecase.GetAllTransactions
import com.lightfeather.domain.usecase.GetTotalExpenseOfCurrency
import com.lightfeather.domain.usecase.GetTotalIncomeOfCurrency
import com.lightfeather.domain.usecase.GetTotalTransactionsByCategories
import com.lightfeather.domain.usecase.UpdateTransaction
import com.lightfeather.domain.model.transaction.Transaction
import com.lightfeather.domain.usecase.CreateAccount
import com.lightfeather.domain.usecase.CreateCategory
import com.lightfeather.domain.usecase.CreateCurrency
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.domain.usecase.GetAllCategories
import com.lightfeather.domain.usecase.GetAllCategoryIcons
import com.lightfeather.domain.usecase.GetAllCurrencies
import com.lightfeather.domain.usecase.GetAllCurrenciesExchangeRates
import com.lightfeather.domain.usecase.GetWealthWorthInCurrency
import com.lightfeather.domain.usecase.UpdateCurrencyExchangeRates
import com.lightfeather.domain.usecase.UpsertUserData

import org.koin.core.qualifier.named
import org.koin.dsl.module


val useCaseModule = module {

    factory { UpsertUserData(get()) }
    factory { GetAllTransactions(get()) }
    factory { GetAllTransactions(get()) }
    factory { GetAllCategories(get()) }
    factory { GetAllAccounts(get()) }

    factory<CreateTransaction>() {
        CreateTransaction(get())
    }


    factory<DeleteTransaction>() {
        DeleteTransaction(get(),)
    }


    factory<UpdateTransaction>() {
        UpdateTransaction(get(),)
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

    factory { GetAllCategoryIcons(get()) }
    factory { CreateCategory(get()) }
    factory { CreateAccount(get()) }
    factory { GetAllCurrencies(get()) }
    factory { GetAllCurrenciesExchangeRates(get()) }
    factory { CreateCurrency(get(), get()) }
    factory { UpdateCurrencyExchangeRates(get()) }
    factory { GetTotalExpenseOfCurrency(get()) }
    factory { GetTotalIncomeOfCurrency(get()) }
    factory { GetWealthWorthInCurrency(get(), get(), get()) }
}