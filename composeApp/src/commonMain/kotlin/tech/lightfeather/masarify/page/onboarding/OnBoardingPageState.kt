package tech.lightfeather.masarify.page.onboarding

import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.model.UiBankName
import tech.lightfeather.designsystem.model.UiCurrency
import dev.icerock.moko.resources.StringResource

internal data class OnBoardingPageState(
    val userName: String = "",
    val accountName: String = "",
    val accountBalance: String = "",
    val selectedCurrency: UiCurrency? = null,
    val accountColor: String = "#FFA726",
    val accountLogo: String = "",
    val appCurrencies: List<UiCurrency> = emptyList(),
    val selectedBank: UiBankName? = null,
    val availableBanks: List<UiBankName> = emptyList(),
) {
    val userNameError: StringResource? = MR.strings.onboarding_user_name_error.takeIf { userName.isBlank() }
    val accountNameError: StringResource? =
        MR.strings.onboarding_account_name_error.takeIf { accountName.isBlank() && selectedBank?.name.isNullOrBlank() }
    val balanceError: StringResource? =
        MR.strings.onboarding_account_balance_error.takeIf { accountBalance.isBlank() }
    val currencyNameError: StringResource? =
        MR.strings.onboarding_account_currency_error.takeIf { selectedCurrency?.name.isNullOrBlank() }
    val isSaveButtonEnabled =
        userNameError == null && accountNameError == null && balanceError == null && currencyNameError == null
}
