package tech.lightfeather.masarify.page.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tech.lightfeather.domain.usecase.GetAllCategories
import tech.lightfeather.masarify.mappers.toCategory
import tech.lightfeather.masarify.mappers.toUiCategory
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.routes.DeleteCategoryRoute
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
        }
    }
}
