package com.lightfeather.designsystem.model

data class UiCategory(
    val id: String,
    val name: String,
    val image: Any,
    val color: String, // Default Material primary color
) {
    companion object {
        val dummy =
            UiCategory(
                id = "1",
                name = "Coffee",
                image = "",
                color = "#4CAF50", // Green color
            )
    }
}
