package com.lightfeather.masarify.di

import com.lightfeather.masarify.app.AppMainViewModel
import com.lightfeather.masarify.navigation.Route
import com.lightfeather.masarify.page.auth.login.LoginPageViewModel
import com.lightfeather.masarify.page.auth.register.RegisterPageViewModel
import com.lightfeather.masarify.page.bankaccounts.BankAccountsPageViewModel
import com.lightfeather.masarify.page.categories.CategoriesPageViewModel
import com.lightfeather.masarify.page.categories.addedit.AddEditCategoryPageViewModel
import com.lightfeather.masarify.page.createbankaccount.CreateBankAccountPageViewModel
import com.lightfeather.masarify.page.currencies.CurrenciesPageViewModel
import com.lightfeather.masarify.page.deletebankaccount.DeleteBankAccountPageViewModel
import com.lightfeather.masarify.page.deletecategory.DeleteCategoryPageViewModel
import com.lightfeather.masarify.page.more.MorePageViewModel
import com.lightfeather.masarify.page.onboarding.OnBoardingPageViewModel
import com.lightfeather.masarify.page.splash.SplashPageViewModel
import com.lightfeather.masarify.page.transactions.TransactionsPageViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val frameworkViewModelModule: Module
val viewModelModule =
    module {
        viewModelOf(::AppMainViewModel)
        viewModelOf(::OnBoardingPageViewModel)
        viewModel { params ->
            SplashPageViewModel(
                navigator = get(),
                userRepository = get(),
                bankAccountRepository = get(),
                seedApplicationData = get(),
                isAuthenticated = get(),
                pendingRoute = runCatching { params.get<Route>() }.getOrNull(),
            )
        }
        viewModelOf(::LoginPageViewModel)
        viewModelOf(::RegisterPageViewModel)
        viewModel {
            BankAccountsPageViewModel(
                navigator = get(),
                getAllAccounts = get(),
                getAllCurrencies = get(),
                getWealthWorthInCurrency = get(),
                exchangeRates = get(),
                deleteTransaction = get(),
                updateTransaction = get(),
                attachmentRepository = get(),
            )
        }
        viewModelOf(::DeleteBankAccountPageViewModel)
        viewModelOf(::CategoriesPageViewModel)
        viewModelOf(::CurrenciesPageViewModel)
        viewModelOf(::DeleteCategoryPageViewModel)
        viewModel {
            TransactionsPageViewModel(
                getAccountsUseCase = get(),
                categoriesUseCase = get(),
                getCurrenciesUseCase = get(),
                createTransactionUseCase = get(),
                updateTransactionUseCase = get(),
                deleteTransactionUseCase = get(),
                attachmentRepository = get(),
                getWealthWorthInCurrency = get(),
                exchangeRates = get(),
                getDefaultAccount = get(),
            )
        }
        viewModel { AddEditCategoryPageViewModel(get(), get(), get(), get(), get(), get(), it.get(), it.get()) }
        viewModel { MorePageViewModel(get(), get(), get(), get(), get(), get(), get()) }
        viewModel {
            CreateBankAccountPageViewModel(
                it.get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
    }
