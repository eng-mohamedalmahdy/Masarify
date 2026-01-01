package com.lightfeather.masarify.page.categories

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object CategoriesList : NavKey

@Serializable
internal data class AddEditCategory(
    val categoryId: String? = null,
) : NavKey
