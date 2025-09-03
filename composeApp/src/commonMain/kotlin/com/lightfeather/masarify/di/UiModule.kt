package com.lightfeather.masarify.di

import androidx.navigation.NavHostController
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.NavigatorImpl
import org.koin.dsl.module


fun uiModule(navHostController: NavHostController) = module {
    single { navHostController }
    single<Navigator> { NavigatorImpl(get()) }
}