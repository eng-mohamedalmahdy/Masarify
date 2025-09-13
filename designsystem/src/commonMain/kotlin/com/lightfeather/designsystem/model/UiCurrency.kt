package com.lightfeather.designsystem.model

data class UiCurrency(
    val id: String,
    val name: String,
    val symbol: String,
) {
    companion object {
        val dummy =
            UiCurrency(
                id = "USD",
                name = "United States Dollar",
                symbol = "$",
            )
    }
}
