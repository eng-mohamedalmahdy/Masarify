package tech.lightfeather.masarify.di

import tech.lightfeather.masarify.app.AppMainViewModel
import tech.lightfeather.masarify.navigation.Route
import tech.lightfeather.masarify.page.auth.login.LoginPageViewModel
import tech.lightfeather.masarify.page.auth.register.RegisterPageViewModel
import tech.lightfeather.masarify.page.bankaccounts.BankAccountsPageViewModel
import tech.lightfeather.masarify.page.categories.CategoriesPageViewModel
import tech.lightfeather.masarify.page.categories.addedit.AddEditCategoryPageViewModel
import tech.lightfeather.masarify.page.createbankaccount.CreateBankAccountPageViewModel
import tech.lightfeather.masarify.page.currencies.CurrenciesPageViewModel
import tech.lightfeather.masarify.page.deletebankaccount.DeleteBankAccountPageViewModel
import tech.lightfeather.masarify.page.deletecategory.DeleteCategoryPageViewModel
import tech.lightfeather.masarify.page.more.MorePageViewModel
import tech.lightfeather.masarify.page.onboarding.OnBoardingPageViewModel
import tech.lightfeather.masarify.page.splash.SplashPageViewModel
import tech.lightfeather.masarify.page.transactions.TransactionsPageViewModel
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
                seedApplicationData = get(),
                acquireAndUpdateFcmUseCase = get(),
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
        viewModelOf(::MorePageViewModel)
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
