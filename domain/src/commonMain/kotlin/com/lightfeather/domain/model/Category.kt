package com.lightfeather.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val name: String,
    val description: String?,
    val color: String,
    val icon: String,
    val isDefault: Boolean = false,
    val resourceKey: String? = null,
) {
    companion object {
        val Transfer =
            Category(
                id = 0,
                name = "Transfer",
                "Transfer Money From Account to Another",
                "#FFBF00",
                "https://img.icons8.com/pastel-glyph/512/transfer-money.png",
                isDefault = true,
                resourceKey = "category_transfer",
            )

        val BalanceAdjustment =
            Category(
                id = -2,
                name = "Balance Adjustment",
                description = "Correct account balance to match real-world amount",
                color = "#FF6B35",
                icon = "https://img.icons8.com/color/512/balance.png",
                isDefault = true,
                resourceKey = "category_balance_adjustment",
            )
    }
}
