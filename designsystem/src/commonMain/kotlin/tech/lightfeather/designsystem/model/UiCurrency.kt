package tech.lightfeather.designsystem.model

data class UiCurrency(
    val id: String,
    val name: String,
    val symbol: String,
    val type: UiCurrencyType = UiCurrencyType.TRADITIONAL,
    val isDefault: Boolean = false,
    val resourceKey: String? = null,
    val isoCode: String? = null,
) {
    companion object {
        val dummy =
            UiCurrency(
                id = "USD",
                name = "United States Dollar",
                symbol = "$",
                type = UiCurrencyType.TRADITIONAL,
                isDefault = true,
                resourceKey = "currency_usd",
            )

        val empty =
            UiCurrency(
                id = "",
                name = "",
                symbol = "",
                type = UiCurrencyType.TRADITIONAL,
                isDefault = false,
                resourceKey = null,
            )
    }
}
