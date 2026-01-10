package com.lightfeather.masarify.page.onboarding

import com.lightfeather.designsystem.MR
import dev.icerock.moko.resources.StringResource

internal data class OnBoardingPageState(
    val userName: String = "",
    val accountName: String = "",
    val accountBalance: String = "",
    val accountCurrencyName: String = "",
    val mainAccountCurrencySymbol: String = "",
    val accountColor: String = "#FFA726",
    val accountLogo: String = "",
) {
    val userNameError: StringResource? = MR.strings.onboarding_user_name_error.takeIf { userName.isBlank() }
    val accountNameError: StringResource? = MR.strings.onboarding_account_name_error.takeIf { accountName.isBlank() }
    val balanceError: StringResource? =
        MR.strings.onboarding_account_balance_error.takeIf { accountBalance.isBlank() }
    val currencyNameError: StringResource? =
        MR.strings.onboarding_account_currency_error.takeIf { accountCurrencyName.isBlank() }
    val isSaveButtonEnabled =
        userNameError == null && accountNameError == null && balanceError == null && currencyNameError == null
}
