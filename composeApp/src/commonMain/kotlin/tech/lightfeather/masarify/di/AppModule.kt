package tech.lightfeather.masarify.di

import tech.lightfeather.data.di.dataModule
import tech.lightfeather.data.di.frameworkModule
import tech.lightfeather.data.di.repositoryModule
import tech.lightfeather.domain.di.useCaseModule
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.dashboard.dashboardModule

// Core modules that don't require parameters
val coreModules =
    listOf(
        dataModule,
        repositoryModule,
        viewModelModule,
        frameworkModule,
        useCaseModule,
        frameworkViewModelModule,
        dashboardModule,
    )

// Function to get all modules including UI module that requires parameters
fun getAppModules(navigator: Navigator) = coreModules + uiModule(navigator)
