package com.lightfeather.core.domain

data class Category(
    val id: Int,
    val name: String,
    val description: String?,
    val color: String,
    val icon: String
) {
    companion object {
        val Transfer = Category(
            id = 0,
            name = "Transfer",
            "Transfer Money From Account to Another",
            "#FFBF00",
            "https://img.icons8.com/pastel-glyph/512/transfer-money.png"

        )
    }

}