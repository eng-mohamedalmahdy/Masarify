package com.lightfeather.masarify.page.categories

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.domain.usecase.GetAllCategories
import com.lightfeather.masarify.mappers.toCategory
import com.lightfeather.masarify.mappers.toUiCategory
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.DeleteCategoryRoute
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CategoriesPageViewModel(
    private val navigator: Navigator,
    private val getAllCategories: GetAllCategories,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            CategoriesPageState(
                categories =
                    getAllCategories().foldResult(
                        onSuccess = { categoriesFlow -> categoriesFlow.map { it.map { it.toUiCategory() } } },
                        onFailure = { flowOf(emptyList()) },
                    ),
            ),
        )
    internal val state: StateFlow<CategoriesPageState> = _state

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    internal fun onIntent(intent: CategoriesPageIntent) {
        when (intent) {
            is CategoriesPageIntent.LoadData -> {
                // Data is already loaded in init, no action needed
            }

            is CategoriesPageIntent.DeleteCategory -> {
                viewModelScope.launch {
                    navigator.navigate(DeleteCategoryRoute(intent.category.toCategory()))
                }
            }

            is CategoriesPageIntent.ClearNavigation -> {
                viewModelScope.launch {
                    Napier.d("ClearNavigation called", tag = "CategoriesPage")
                    while (intent.navigator.canNavigateBack()) {
                        intent.navigator.navigateBack()
                    }
                    _state.value = _state.value.copy(selectedCategory = null)
                    Napier.d("After clear: selectedCategory = null", tag = "CategoriesPage")
                }
            }

            is CategoriesPageIntent.NavigationIntent -> {
                viewModelScope.launch {
                    while (intent.navigator.canNavigateBack()) {
                        intent.navigator.navigateBack()
                    }
                    _state.value = _state.value.copy(selectedCategory = UiCategory.empty)
                    intent.navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, intent)
                    _state.value = _state.value.copy(selectedCategory = intent.category)
                }
            }
        }
    }
}
