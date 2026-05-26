package tech.lightfeather.masarify.page.transactions

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TransactionsPageContentTest {
    @Test
    fun filterButtonIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    TransactionsListTopBar(
                        totalAmountInSelectedOrDefaultCurrency = "0.0",
                        userAccountsCurrencies = emptyList(),
                        defaultCurrency = null,
                        selectedCurrency = null,
                        totalAccounts = 0,
                        activeFilterCount = 0,
                        onFilterClick = {},
                        onCurrencyClick = {},
                    )
                }
            }

            onNodeWithTag("transactions_filter_button").assertIsDisplayed()
        }

    @Test
    fun filterButtonShowsBadgeWhenFiltersActive() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    TransactionsListTopBar(
                        totalAmountInSelectedOrDefaultCurrency = "0.0",
                        userAccountsCurrencies = emptyList(),
                        defaultCurrency = null,
                        selectedCurrency = null,
                        totalAccounts = 0,
                        activeFilterCount = 2,
                        onFilterClick = {},
                        onCurrencyClick = {},
                    )
                }
            }

            onNodeWithTag("transactions_filter_button").assertIsDisplayed()
        }
}
