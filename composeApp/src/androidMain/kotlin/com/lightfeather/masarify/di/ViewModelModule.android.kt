package com.lightfeather.masarify.di

import com.lightfeather.masarify.template.transactionspane.TransactionsPanePageViewModel
import com.lightfeather.masarify.template.transactionspane.TransactionsPagingSource
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val frameworkViewModelModule = module {
    factory<() -> TransactionsPagingSource> {
        { TransactionsPagingSource(get()) }
    }

    viewModelOf(::TransactionsPanePageViewModel)
}
