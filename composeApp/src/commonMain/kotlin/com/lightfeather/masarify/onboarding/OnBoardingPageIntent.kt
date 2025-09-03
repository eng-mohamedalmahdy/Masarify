package com.lightfeather.masarify.onboarding

internal sealed interface OnBoardingPageIntent {
    data class UpdateUserName(val name: String) : OnBoardingPageIntent
    data class UpdateAccountName(val name: String) : OnBoardingPageIntent
    data class UpdateCurrencyName(val name: String) : OnBoardingPageIntent
    data class UpdateCurrencySymbol(val name: String) : OnBoardingPageIntent
    data object Submit : OnBoardingPageIntent
}