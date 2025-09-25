package com.lightfeather.masarify.di

import com.lightfeather.masarify.template.transactionspane.TransactionsPanePageViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val frameworkViewModelModule: Module =
    module {
        viewModel {
            TransactionsPanePageViewModel(
                getAllTransactionsPaged = get(),
                getFilteredTransactionsPaged = get(),
                getTransactionCount = get(),
            )
        }
    }
