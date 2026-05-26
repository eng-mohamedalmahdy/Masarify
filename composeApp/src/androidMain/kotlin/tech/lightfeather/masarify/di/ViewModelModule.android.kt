package tech.lightfeather.masarify.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import tech.lightfeather.domain.repository.FCMHelper
import tech.lightfeather.masarify.fcm.AndroidFcmHelper
import tech.lightfeather.masarify.template.transactionspane.TransactionsPaneViewModel
import tech.lightfeather.masarify.widget.WidgetDataSyncService

actual val frameworkViewModelModule =
    module {
        // TransactionsPanePageViewModel removed - pane is now stateless
        // Paging data source is created in TransactionsPageViewModel

        viewModel { TransactionsPaneViewModel(it.get(), get()) }
        single { WidgetDataSyncService(androidContext()) }
        single<FCMHelper> { AndroidFcmHelper() }
    }
