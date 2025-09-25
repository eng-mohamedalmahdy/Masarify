package com.lightfeather.masarify.di

import com.lightfeather.masarify.template.transactionspane.TransactionsPagingSource
import com.lightfeather.masarify.template.transactionspane.TransactionsPanePageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

actual val frameworkViewModelModule =
    module {
        factory<() -> TransactionsPagingSource> {
            { TransactionsPagingSource(get(), get(), it.get()) }
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
