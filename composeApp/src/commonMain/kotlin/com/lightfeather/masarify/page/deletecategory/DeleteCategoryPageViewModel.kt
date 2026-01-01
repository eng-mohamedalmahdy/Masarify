package com.lightfeather.masarify.page.deletecategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.usecase.DeleteCategory
import com.lightfeather.masarify.MR
import com.lightfeather.masarify.navigation.Navigator
import kotlinx.coroutines.launch

class DeleteCategoryPageViewModel(
    private val navigator: Navigator,
    private val deleteCategory: DeleteCategory,
    toBeDeletedCategory: Category,
) : ViewModel() {
    val category = toBeDeletedCategory
    private val categoryToDelete = toBeDeletedCategory

    fun onConfirm() {
        viewModelScope.launch {
            deleteCategory(categoryToDelete).foldResult(
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
