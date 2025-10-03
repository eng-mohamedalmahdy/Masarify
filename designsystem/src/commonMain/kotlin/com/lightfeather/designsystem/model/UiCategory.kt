package com.lightfeather.designsystem.model

data class UiCategory(
    val id: String,
    val name: String,
    val description: String? = null,
    val image: Any,
    val color: String,
) {
    companion object {
        val dummy =
            UiCategory(
                id = "1",
                name = "Coffee",
                description = "Morning coffee and beverages",
                image = "",
                color = "#4CAF50",
            )

        val empty =
            UiCategory(
                id = "",
                name = "",
                description = null,
                image = "",
                color = "#000000",
            )
    }
}
