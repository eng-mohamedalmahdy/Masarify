package tech.lightfeather.masarify.page.deletecategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.domain.model.Category
import tech.lightfeather.domain.usecase.DeleteCategory
import tech.lightfeather.masarify.navigation.Navigator

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
