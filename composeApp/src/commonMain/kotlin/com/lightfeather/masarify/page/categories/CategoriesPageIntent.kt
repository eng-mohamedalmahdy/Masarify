package com.lightfeather.masarify.page.categories

import com.lightfeather.designsystem.model.UiCategory

internal sealed interface CategoriesPageIntent {
    data object LoadData : CategoriesPageIntent

    sealed interface NavigationIntent : CategoriesPageIntent {
        data class AddCategory(
            val category: UiCategory = UiCategory.empty,
        ) : NavigationIntent

        data class UpdateCategory(
            val selectedCategory: UiCategory,
        ) : NavigationIntent
    }

    data class DeleteCategory(
        val category: UiCategory,
    ) : CategoriesPageIntent

    data object ClearNavigation : CategoriesPageIntent
}
