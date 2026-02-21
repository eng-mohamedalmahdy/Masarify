package com.lightfeather.masarify.page.dashboard

import org.koin.core.module.dsl.viewModel
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
                getFilteredTransactionsPaged = get(),
                getFilteredTransactions = get(),
                getAllCategories = get(),
                deleteTransaction = get(),
                updateTransaction = get(),
                attachmentRepository = get(),
                userRepository = get(),
                navigator = get(),
            )
        }
    }
