package tech.lightfeather.masarify.di

import tech.lightfeather.masarify.navigation.Navigator
import org.koin.dsl.module

fun uiModule(navigator: Navigator) =
    module {
        single<Navigator> { navigator }
    }
