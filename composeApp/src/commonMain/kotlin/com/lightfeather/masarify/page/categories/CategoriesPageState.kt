package com.lightfeather.masarify.page.categories

import com.lightfeather.designsystem.model.UiCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal data class CategoriesPageState(
    val categories: Flow<List<UiCategory>> = emptyFlow(),
    val isLoading: Boolean = false,
)
