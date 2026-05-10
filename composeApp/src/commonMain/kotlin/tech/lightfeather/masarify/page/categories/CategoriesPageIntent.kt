package tech.lightfeather.masarify.page.categories

import tech.lightfeather.designsystem.model.UiCategory

internal sealed interface CategoriesPageIntent {
    data object LoadData : CategoriesPageIntent

    data class DeleteCategory(
        val category: UiCategory,
    ) : CategoriesPageIntent
}
