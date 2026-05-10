package tech.lightfeather.masarify.page.createbankaccount

import tech.lightfeather.designsystem.model.UiBankName
import tech.lightfeather.designsystem.model.UiCurrency

data class CreateBankAccountPageState(
    val name: String = "",
    val description: String = "",
    val initialBalance: String = "",
    val color: String = "#FFFFFF",
    val logo: String = "",
    val accountId: String? = null,
    val currency: UiCurrency? = null,
    val isLoading: Boolean = false,
    val availableCurrencies: List<UiCurrency> = emptyList(),
    val savedColors: List<String> = emptyList(),
    val selectedBank: UiBankName? = null,
    val availableBanks: List<UiBankName> = emptyList(),
    val isDefault: Boolean = false,
) {
    val inEditMode: Boolean get() = accountId != null
}
