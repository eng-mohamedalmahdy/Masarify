package com.lightfeather.masarify.page.onboarding

import com.lightfeather.designsystem.model.UiCurrency

internal sealed interface OnBoardingPageIntent {
    data class UpdateUserName(
        val name: String,
    ) : OnBoardingPageIntent

    data class UpdateAccountName(
        val name: String,
    ) : OnBoardingPageIntent

    data class UpdateCurrency(
        val currency: UiCurrency,
    ) : OnBoardingPageIntent

    data class UpdateAccountBalance(
        val balance: String,
    ) : OnBoardingPageIntent

    data object Submit : OnBoardingPageIntent
    data class AddNewCurrency(
        val currency: UiCurrency,
    ) : OnBoardingPageIntent
}
