package tech.lightfeather.masarify.page.onboarding

import tech.lightfeather.designsystem.model.UiBankName
import tech.lightfeather.designsystem.model.UiCurrency

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

    data class SelectBank(
        val bank: UiBankName,
    ) : OnBoardingPageIntent

    data class AddNewBank(
        val bankName: String,
    ) : OnBoardingPageIntent

    data object NavigateToSignIn : OnBoardingPageIntent
}
