package tech.lightfeather.masarify.page.currencies

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CurrenciesPageContentTest {
    @Test
    fun addFabIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    CurrenciesPageContent(
                        state = CurrenciesPageState(),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("currencies_add_fab").assertIsDisplayed()
        }

    @Test
    fun emptyStateIsDisplayedWhenNoCurrencies() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    CurrenciesPageContent(
                        state = CurrenciesPageState(),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("currencies_empty_state").assertIsDisplayed()
        }

    @Test
    fun addCurrencyDialogIsDisplayedWhenShowAddEditDialogIsTrue() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    CurrenciesPageContent(
                        state = CurrenciesPageState(showAddEditDialog = true),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("add_edit_currency_dialog").assertIsDisplayed()
        }
}
