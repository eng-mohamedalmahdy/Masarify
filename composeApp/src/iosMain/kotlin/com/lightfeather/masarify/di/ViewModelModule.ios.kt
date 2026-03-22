package com.lightfeather.masarify.di

import com.lightfeather.masarify.template.transactionspane.TransactionsPaneViewModel
import com.lightfeather.masarify.widget.WidgetDataSyncService
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val frameworkViewModelModule: Module =
    module {
        viewModel { TransactionsPaneViewModel(it.get(), get()) }
        single { WidgetDataSyncService() }
    }
