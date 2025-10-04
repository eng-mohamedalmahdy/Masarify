package com.lightfeather.designsystem.model

data class UiCurrencyExchangeRate(
    val id: String,
    val fromCurrency: UiCurrency,
    val toCurrency: UiCurrency,
    val rate: Double,
) {
    val inverseRate: Double
        get() = if (rate != 0.0) 1.0 / rate else 0.0

    companion object {
        val dummy =
            UiCurrencyExchangeRate(
                id = "1",
                fromCurrency = UiCurrency(id = "USD", name = "US Dollar", symbol = "$"),
                toCurrency = UiCurrency(id = "EUR", name = "Euro", symbol = "€"),
                rate = 0.92,
            )

        val empty =
            UiCurrencyExchangeRate(
                id = "",
                fromCurrency = UiCurrency.empty,
                toCurrency = UiCurrency.empty,
                rate = 0.0,
            )
    }
}
