package com.lightfeather.masarify.di

import com.lightfeather.masarify.app.AppMainViewModel
import com.lightfeather.masarify.page.bankaccounts.BankAccountsPageViewModel
import com.lightfeather.masarify.page.createbankaccount.CreateBankAccountPageViewModel
import com.lightfeather.masarify.page.onboarding.OnBoardingPageViewModel
import com.lightfeather.masarify.page.splash.SplashPageViewModel
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
        viewModel { CreateBankAccountPageViewModel(it.get(), get(), get(), get()) }
    }
