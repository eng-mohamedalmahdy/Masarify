package com.lightfeather.masarify.di

import com.lightfeather.masarify.app.AppMainViewModel
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
        viewModelOf(::SplashPageViewModel)
        viewModelOf(::BankAccountsPageViewModel)
        viewModelOf(::DeleteBankAccountPageViewModel)
        viewModelOf(::CategoriesPageViewModel)
        viewModelOf(::CurrenciesPageViewModel)
        viewModelOf(::DeleteCategoryPageViewModel)
        viewModel {
            TransactionsPageViewModel(
                getAccountsUseCase = get(),
                categoriesUseCase = get(),
                getCurrenciesUseCase = get(),
                getFilteredTransactionsPaged = get(),
                getFilteredTransactionCount = get(),
                createTransactionUseCase = get(),
                updateTransactionUseCase = get(),
                deleteTransactionUseCase = get(),
                sharedDatabase = getOrNull(), // Only available on Android/iOS
            )
        }
        viewModel { AddEditCategoryPageViewModel(get(), get(), get(), get(), get(), it.get(), it.get()) }
        viewModel { MorePageViewModel(get(), get()) }
        viewModel { CreateBankAccountPageViewModel(it.get(), get(), get(), get(), get(), get(), get(), get()) }
    }
