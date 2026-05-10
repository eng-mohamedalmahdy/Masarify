package tech.lightfeather.designsystem.model

import kotlinx.datetime.LocalDateTime

/**
 * Represents a saved transaction filter with a user-defined name
 * Allows users to save and quickly apply their favorite filter configurations
 */
data class SavedFilter(
    val id: String,
    val name: String,
    val filter: UiTransactionFilter,
    val createdAt: LocalDateTime,
) {
    companion object {
        val empty =
            SavedFilter(
                id = "",
                name = "",
                filter = UiTransactionFilter.EMPTY,
                createdAt = LocalDateTime(2024, 1, 1, 0, 0),
            )
    }
}
