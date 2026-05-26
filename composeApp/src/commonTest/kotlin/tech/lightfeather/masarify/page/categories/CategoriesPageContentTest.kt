package tech.lightfeather.masarify.page.categories

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CategoriesPageContentTest {
    @Test
    fun addCategoryFabIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    CategoriesListPane(
                        categories = emptyList(),
                        selectedCategory = null,
                        onAddCategory = {},
                        onCategoryClick = {},
                        onDeleteCategory = {},
                    )
                }
            }

            onNodeWithTag("categories_add_fab").assertIsDisplayed()
        }

    @Test
    fun emptyStateIsDisplayedWhenNoCategoriesExist() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    CategoriesListPane(
                        categories = emptyList(),
                        selectedCategory = null,
                        onAddCategory = {},
                        onCategoryClick = {},
                        onDeleteCategory = {},
                    )
                }
            }

            onNodeWithTag("categories_add_fab").assertIsDisplayed()
        }
}
