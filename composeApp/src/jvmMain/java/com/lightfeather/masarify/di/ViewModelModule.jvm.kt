package com.lightfeather.masarify.di

import com.lightfeather.masarify.template.transactionspane.TransactionsPanePageViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val frameworkViewModelModule: Module = module {
    viewModelOf(::TransactionsPanePageViewModel)
}
