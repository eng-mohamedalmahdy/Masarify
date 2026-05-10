package tech.lightfeather.masarify.di

import tech.lightfeather.domain.repository.FCMHelper
import tech.lightfeather.masarify.fcm.IosFcmHelper
import tech.lightfeather.masarify.template.transactionspane.TransactionsPaneViewModel
import tech.lightfeather.masarify.widget.WidgetDataSyncService
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val frameworkViewModelModule: Module =
    module {
        viewModel { TransactionsPaneViewModel(it.get(), get()) }
        single { WidgetDataSyncService() }
        single<FCMHelper> { IosFcmHelper(get()) }
    }
