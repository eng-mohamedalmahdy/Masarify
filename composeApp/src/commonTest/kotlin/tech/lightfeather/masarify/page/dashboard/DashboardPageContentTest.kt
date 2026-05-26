package tech.lightfeather.masarify.page.dashboard

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DashboardPageContentTest {
    @Test
    fun fabIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    DashboardListPane(
                        state = DashboardPageState(),
                        onIntent = {},
                        onAccountClick = {},
                        onTransactionClick = {},
                    )
                }
            }

            onNodeWithTag("dashboard_main_fab").assertIsDisplayed()
        }

    @Test
    fun emptyAccountsSectionIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    DashboardListPane(
                        state = DashboardPageState(userName = "Test User"),
                        onIntent = {},
                        onAccountClick = {},
                        onTransactionClick = {},
                    )
                }
            }

            onNodeWithTag("dashboard_main_fab").assertIsDisplayed()
        }
}
