package com.lightfeather.masarify.di

import com.lightfeather.masarify.app.AppMainViewModel
import com.lightfeather.masarify.pages.onboarding.OnBoardingPageViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AppMainViewModel)
    viewModelOf(::OnBoardingPageViewModel)
}