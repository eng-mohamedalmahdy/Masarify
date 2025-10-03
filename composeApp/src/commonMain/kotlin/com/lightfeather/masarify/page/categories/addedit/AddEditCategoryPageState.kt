package com.lightfeather.masarify.page.categories.addedit

import com.lightfeather.designsystem.model.UiCategory

internal data class AddEditCategoryPageState(
    val categoryId: Int? = null,
    val name: String = "",
    val description: String = "",
    val selectedColor: String = "#4CAF50",
    val selectedIcon: Any? = null,
    val customIconUrl: String = "",
    val recentColors: List<String> = emptyList(),
    val availableIcons: List<Any> = emptyList(),
    val isLoadingIcons: Boolean = false,
    val isLoading: Boolean = false,
    val showColorPicker: Boolean = false,
) {
    val isEditMode: Boolean get() = categoryId != null
    val isValid: Boolean get() = name.isNotBlank() && (selectedIcon != null || customIconUrl.isNotBlank())

    companion object {
        fun fromCategory(category: UiCategory) =
            AddEditCategoryPageState(
                categoryId = category.id.toInt(),
                name = category.name,
                description = category.description.orEmpty(),
                selectedColor = category.color,
                selectedIcon = category.image,
            )
    }
}
