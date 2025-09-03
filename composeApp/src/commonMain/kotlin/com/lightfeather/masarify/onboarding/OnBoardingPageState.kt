package com.lightfeather.masarify.onboarding

internal data class OnBoardingPageState(
    val userName: String = "",
    val accountName: String = "",
    val accountBalance: String = "",
    val accountCurrencyName: String = "",
    val mainAccountCurrencySymbol: String = "",
    val accountColor: String = "#000000",
    val accountLogo: String = "",
)