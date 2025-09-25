package com.lightfeather.masarify.di

import com.lightfeather.masarify.template.transactionspane.TransactionsPagingSource
import com.lightfeather.masarify.template.transactionspane.TransactionsPanePageViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val frameworkViewModelModule: Module =
    module {
        factory<() -> TransactionsPagingSource> {
            { TransactionsPagingSource(get()) }
        }

        viewModel {
            TransactionsPanePageViewModel(
                getAllTransactionsPaged = get(),
                getFilteredTransactionsPaged = get(),
                getTransactionCount = get(),
                transactionsPagingSourceFactory = get(),
                sharedDatabase = get(),
            )
        }
    }
