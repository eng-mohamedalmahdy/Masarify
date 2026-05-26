package tech.lightfeather.masarify.page.categories.addedit

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AddEditCategoryPageContentTest {
    @Test
    fun nameFieldIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    AddEditCategoryPageContent(
                        state = AddEditCategoryPageState(),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("add_edit_category_name_field").assertIsDisplayed()
        }

    @Test
    fun saveButtonIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    AddEditCategoryPageContent(
                        state = AddEditCategoryPageState(),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("add_edit_category_save_button").assertIsDisplayed()
        }

    @Test
    fun cancelButtonIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    AddEditCategoryPageContent(
                        state = AddEditCategoryPageState(),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("add_edit_category_cancel_button").assertIsDisplayed()
        }
}
