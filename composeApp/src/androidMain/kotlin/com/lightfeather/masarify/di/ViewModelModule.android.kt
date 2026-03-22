package com.lightfeather.masarify.di

import com.lightfeather.masarify.template.transactionspane.TransactionsPaneViewModel
import com.lightfeather.masarify.widget.WidgetDataSyncService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val frameworkViewModelModule =
    module {
        // TransactionsPanePageViewModel removed - pane is now stateless
        // Paging data source is created in TransactionsPageViewModel

        viewModel { TransactionsPaneViewModel(it.get(), get()) }
        single { WidgetDataSyncService(androidContext()) }
    }
