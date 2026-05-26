package tech.lightfeather.masarify.page.categories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import tech.lightfeather.designsystem.model.UiCategory

internal data class CategoriesPageState(
    val categories: Flow<List<UiCategory>> = emptyFlow(),
    val isLoading: Boolean = false,
)
