package tech.lightfeather.designsystem.model

import androidx.compose.runtime.Composable
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
    val isDefault: Boolean = false,
) {
    val localizedName: String @Composable get() = name.getBankLocalizedName()

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

        val empty =
            UiBankAccount(
                id = "",
                name = "",
                description = null,
                balance = "",
                currency = UiCurrency.empty,
                color = "",
                image = null,
            )
    }
}
