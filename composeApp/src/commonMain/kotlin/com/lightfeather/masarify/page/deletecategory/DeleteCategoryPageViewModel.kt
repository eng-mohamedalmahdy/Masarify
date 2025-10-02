package com.lightfeather.masarify.page.deletecategory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.usecase.DeleteCategory
import com.lightfeather.masarify.MR
import com.lightfeather.masarify.navigation.NavTypeProvider
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.DeleteCategoryRoute
import kotlinx.coroutines.launch

class DeleteCategoryPageViewModel(
    private val navigator: Navigator,
    private val deleteCategory: DeleteCategory,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val toBeDeletedCategory =
        savedStateHandle
            .toRoute<DeleteCategoryRoute>(
                typeMap = mapOf(NavTypeProvider.provideMapEntry<Category>()),
            ).category

    val category = toBeDeletedCategory

    fun onConfirm() {
        viewModelScope.launch {
            deleteCategory(toBeDeletedCategory).foldResult(
                onSuccess = {
                    SnackbarService.sendSuccessMessage(MR.strings.category_delete_success)
                    navigator.navigateUp()
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.category_delete_failure)
                    navigator.navigateUp()
                },
            )
        }
    }

    fun onCancel() {
        navigator.navigateUp()
    }
}
