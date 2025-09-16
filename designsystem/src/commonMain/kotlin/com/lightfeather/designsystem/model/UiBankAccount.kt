package com.lightfeather.designsystem.model

import kotlinx.serialization.Serializable

@Serializable
data class UiBankAccount(
    val id: String,
    val name: String,
    val description: String? = null,
    val balance: String,
    val currency: UiCurrency,
    val color: String,
    val image: String?,
) {
    companion object {
        val dummy =
            UiBankAccount(
                id = "1",
                name = "My Bank Account",
                description = "My personal bank account",
                balance = "1,000.00",
                currency = UiCurrency.dummy,
                color = "#FFFFFF",
                image = null,
            )
    }
}
