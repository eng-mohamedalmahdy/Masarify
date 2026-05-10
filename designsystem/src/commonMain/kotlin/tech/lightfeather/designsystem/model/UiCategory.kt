package tech.lightfeather.designsystem.model

data class UiCategory(
    val id: String,
    val name: String,
    val description: String? = null,
    val image: Any,
    val color: String,
    val isDefault: Boolean = false,
    val resourceKey: String? = null,
) {
    companion object {
        val dummy =
            UiCategory(
                id = "1",
                name = "Coffee",
                description = "Morning coffee and beverages",
                image = "",
                color = "#4CAF50",
                isDefault = false,
                resourceKey = null,
            )

        val empty =
            UiCategory(
                id = "",
                name = "",
                description = null,
                image = "",
                color = "#000000",
                isDefault = false,
                resourceKey = null,
            )
    }
}
