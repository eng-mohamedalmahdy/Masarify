package tech.lightfeather.designsystem.model

/**
 * Represents page size options for paginated lists
 * Provides common page sizes with display labels
 */
enum class PageSize(
    val value: Int,
    val label: String,
) {
    SMALL(10, "10"),
    MEDIUM(20, "20"),
    LARGE(50, "50"),
    EXTRA_LARGE(100, "100"),
    ;

    companion object {
        val DEFAULT = MEDIUM

        fun fromValue(value: Int): PageSize = entries.find { it.value == value } ?: DEFAULT
    }
}
