package com.lightfeather.domain.di

import com.lightfeather.domain.usecase.CreateTransaction
import com.lightfeather.domain.usecase.DeleteTransaction
import com.lightfeather.domain.usecase.GetAllTransactions
import com.lightfeather.domain.usecase.GetTotalExpenseOfCurrency
import com.lightfeather.domain.usecase.GetTotalIncomeOfCurrency
import com.lightfeather.domain.usecase.GetTotalTransactionsByCategories
import com.lightfeather.domain.usecase.UpdateTransaction
import com.lightfeather.domain.domain.transaction.Transaction
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

import org.koin.core.qualifier.named
import org.koin.dsl.module


val useCaseModule = module {
    factory { GetAllTransactions(get()) }
    factory { GetAllTransactions(get()) }
    factory { GetAllCategories(get()) }
    factory { GetAllAccounts(get()) }

    factory<CreateTransaction<Transaction.Expense>>(named("expense")) {
        CreateTransaction(
            get<ExpensesRepository>(),

        )
    }
    factory<CreateTransaction<Transaction.Income>>(named("income")) {
        CreateTransaction(get<IncomeRepository>(),)
    }
    factory<CreateTransaction<Transaction.Transfer>>(named("transfer")) {
        CreateTransaction(
            get<TransferRepository>(),

        )
    }


    factory<DeleteTransaction<Transaction.Expense>>(named("expense")) {
        DeleteTransaction(
            get<ExpensesRepository>(),

        )
    }
    factory<DeleteTransaction<Transaction.Income>>(named("income")) {
        DeleteTransaction(get<IncomeRepository>(),)
    }
    factory<DeleteTransaction<Transaction.Transfer>>(named("transfer")) {
        DeleteTransaction(get<TransferRepository>(),)
    }

    factory<UpdateTransaction<Transaction.Expense>>(named("expense")) {
        UpdateTransaction(get<ExpensesRepository>(),)
    }
    factory<UpdateTransaction<Transaction.Income>>(named("income")) {
        UpdateTransaction(get<IncomeRepository>(),)
    }
    factory<UpdateTransaction<Transaction.Transfer>>(named("transfer")) {
        UpdateTransaction(get<TransferRepository>(),)
    }

    factory<GetTotalTransactionsByCategories<Transaction.Expense>>(named("expense")) {
        GetTotalTransactionsByCategories(get<ExpensesRepository>())
    }

    factory<GetTotalTransactionsByCategories<Transaction.Income>>(named("income")) {
        GetTotalTransactionsByCategories(get<IncomeRepository>())
    }

    factory<GetTotalTransactionsByCategories<Transaction.Transfer>>(named("transfer")) {
        GetTotalTransactionsByCategories(get<TransferRepository>())
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