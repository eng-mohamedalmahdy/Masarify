package com.lightfeather.masarify.page.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                Napier.d("ClearNavigation called", tag = "CategoriesPage")
                _state.value = _state.value.copy(selectedCategory = null)
                Napier.d("After clear: selectedCategory = null", tag = "CategoriesPage")
            }

            is CategoriesPageIntent.NavigationIntent.AddCategory -> {
                Napier.d("AddCategory called", tag = "CategoriesPage")
                _state.value = _state.value.copy(selectedCategory = intent.category)
            }

            is CategoriesPageIntent.NavigationIntent.UpdateCategory -> {
                Napier.d("UpdateCategory called for ${intent.selectedCategory.name}", tag = "CategoriesPage")
                _state.value = _state.value.copy(selectedCategory = intent.selectedCategory)
            }
        }
    }
}
