package com.lightfeather.masarify.page.deletecategory

import androidx.compose.runtime.Composable
import com.lightfeather.designsystem.component.molecules.dialog.AppAlertDialog
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.domain.model.Category
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DeleteCategoryPage(category: Category) {
    val viewModel: DeleteCategoryPageViewModel = koinViewModel { parametersOf(category) }
    val toBeDeletedCategory = viewModel.category
    DeleteCategoryPageContent(
        toBeDeletedCategory = toBeDeletedCategory,
        onConfirm = viewModel::onConfirm,
        onCancel = viewModel::onCancel,
    )
}

@Composable
internal fun DeleteCategoryPageContent(
    toBeDeletedCategory: Category,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    AppAlertDialog(
        title = stringResource(MR.strings.delete_category_dialog_title),
        message =
            stringResource(
                MR.strings.delete_category_dialog_description_with_name,
                toBeDeletedCategory.name,
            ),
        onConfirm = onConfirm,
        onDismissRequest = onCancel,
    )
}

@Preview
@Composable
fun DeleteCategoryPagePreview() {
    AppTheme {
        DeleteCategoryPageContent(
            toBeDeletedCategory =
                Category(
                    id = 1,
                    name = "Food",
                    description = "Food expenses",
                    color = "#FF5722",
                    icon = "https://img.icons8.com/ios/50/food.png",
                ),
            onConfirm = {},
            onCancel = {},
        )
    }
}
