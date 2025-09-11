package com.lightfeather.masarify.di

import androidx.navigation.NavHostController
import com.lightfeather.data.di.dataModule
import com.lightfeather.data.di.frameworkModule
import com.lightfeather.data.di.repositoryModule
import com.lightfeather.domain.di.useCaseModule

// Core modules that don't require parameters
val coreModules =
    listOf(
        dataModule,
        repositoryModule,
        viewModelModule,
        frameworkModule,
        useCaseModule,
    )

// Function to get all modules including UI module that requires parameters
fun getAppModules(navHostController: NavHostController) = coreModules + uiModule(navHostController)
