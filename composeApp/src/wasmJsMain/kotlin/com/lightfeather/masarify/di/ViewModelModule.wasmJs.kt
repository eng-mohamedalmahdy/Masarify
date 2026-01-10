package com.lightfeather.masarify.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual val frameworkViewModelModule: Module =
    module {
        // TransactionsPanePageViewModel removed - pane is now stateless
        // All state and logic is managed by TransactionsPageViewModel
    }
