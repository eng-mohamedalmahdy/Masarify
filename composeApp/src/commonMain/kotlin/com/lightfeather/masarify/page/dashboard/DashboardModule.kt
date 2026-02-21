package com.lightfeather.masarify.page.dashboard

import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for Dashboard page dependencies
 */
val dashboardModule =
    module {
        viewModel {
            DashboardPageViewModel(
                getAllAccounts = get(),
                getWealthWorthInCurrency = get(),
                getAllTransactionsPaged = get(),
                getFilteredTransactionsPaged = get(),
                getFilteredTransactions = get(),
                getTotalExpenseOfCurrency = get(),
                getTotalIncomeOfCurrency = get(),
                getTotalExpensesByCategories = get(named("expense")),
                getExchangeRatesOfCurrency = get(),
                getAllCategories = get(),
                deleteTransaction = get(),
                updateTransaction = get(),
                attachmentRepository = get(),
                userRepository = get(),
                navigator = get(),
            )
        }
    }
