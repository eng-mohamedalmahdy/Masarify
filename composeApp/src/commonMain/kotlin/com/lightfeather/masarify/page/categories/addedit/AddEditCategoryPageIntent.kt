package com.lightfeather.masarify.page.categories.addedit

internal sealed interface AddEditCategoryPageIntent {
    data object LoadData : AddEditCategoryPageIntent

    data class UpdateName(val name: String) : AddEditCategoryPageIntent

    data class UpdateDescription(val description: String) : AddEditCategoryPageIntent

    data class UpdateCustomIconUrl(val url: String) : AddEditCategoryPageIntent

    data class SelectColor(val color: String) : AddEditCategoryPageIntent

    data class SelectIcon(val icon: Any) : AddEditCategoryPageIntent

    data class ToggleColorPicker(val show: Boolean) : AddEditCategoryPageIntent

    data object Save : AddEditCategoryPageIntent

    data object Cancel : AddEditCategoryPageIntent

    data class SaveRecentColor(val color: String) : AddEditCategoryPageIntent
}
