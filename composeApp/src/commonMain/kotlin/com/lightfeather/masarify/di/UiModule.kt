package com.lightfeather.masarify.di

import com.lightfeather.masarify.navigation.Navigator
import org.koin.dsl.module

fun uiModule(navigator: Navigator) =
    module {
        single<Navigator> { navigator }
    }
