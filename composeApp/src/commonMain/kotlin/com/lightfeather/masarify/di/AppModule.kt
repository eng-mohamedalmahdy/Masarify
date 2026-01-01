package com.lightfeather.masarify.di

import com.lightfeather.data.di.dataModule
import com.lightfeather.data.di.frameworkModule
import com.lightfeather.data.di.repositoryModule
import com.lightfeather.domain.di.useCaseModule
import com.lightfeather.masarify.navigation.Navigator

// Core modules that don't require parameters
val coreModules =
    listOf(
        dataModule,
        repositoryModule,
        viewModelModule,
        frameworkModule,
        useCaseModule,
        frameworkViewModelModule,
    )

// Function to get all modules including UI module that requires parameters
fun getAppModules(navigator: Navigator) = coreModules + uiModule(navigator)
