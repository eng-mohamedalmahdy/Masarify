package com.lightfeather.masarify.page.categories

import com.lightfeather.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal data class CategoriesPageState(
    val categories: Flow<List<Category>> = emptyFlow(),
    val selectedCategory: Category? = null,
    val isLoading: Boolean = false,
)
