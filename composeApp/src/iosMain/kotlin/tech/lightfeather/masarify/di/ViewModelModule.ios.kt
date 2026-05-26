package tech.lightfeather.masarify.di

import dev.brewkits.kmpworkmanager.background.data.IosWorkerFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import tech.lightfeather.domain.repository.FCMHelper
import tech.lightfeather.masarify.fcm.IosFcmHelper
import tech.lightfeather.masarify.notification.AppIosWorkerFactory
import tech.lightfeather.masarify.template.transactionspane.TransactionsPaneViewModel
import tech.lightfeather.masarify.widget.WidgetDataSyncService

actual val frameworkViewModelModule: Module =
    module {
        viewModel { TransactionsPaneViewModel(it.get(), get()) }
        single { WidgetDataSyncService() }
        single<FCMHelper> { IosFcmHelper(get()) }
        single<IosWorkerFactory> { AppIosWorkerFactory() }
    }
