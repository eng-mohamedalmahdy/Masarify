package tech.lightfeather.masarify.di

import org.koin.dsl.module
import tech.lightfeather.masarify.navigation.Navigator

fun uiModule(navigator: Navigator) =
    module {
        single<Navigator> { navigator }
    }
