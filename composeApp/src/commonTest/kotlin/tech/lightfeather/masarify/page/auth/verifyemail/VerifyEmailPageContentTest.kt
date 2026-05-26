package tech.lightfeather.masarify.page.auth.verifyemail

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class VerifyEmailPageContentTest {
    @Test
    fun successStateShowsGoToDashboardButton() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    VerifyEmailPageContent(
                        state = VerifyEmailPageState(isLoading = false, isSuccess = true),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("verify_email_go_to_dashboard_button").assertIsDisplayed()
        }

    @Test
    fun errorStateShowsGoToDashboardButton() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    VerifyEmailPageContent(
                        state = VerifyEmailPageState(isLoading = false, isError = true),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("verify_email_go_to_dashboard_button").assertIsDisplayed()
        }

    @Test
    fun clickGoToDashboardFiresGoToDashboardIntent() =
        runComposeUiTest {
            val capturedIntents = mutableListOf<VerifyEmailPageIntent>()
            setContent {
                AppTheme(useDarkTheme = false) {
                    VerifyEmailPageContent(
                        state = VerifyEmailPageState(isLoading = false, isSuccess = true),
                        onIntent = capturedIntents::add,
                    )
                }
            }

            onNodeWithTag("verify_email_go_to_dashboard_button").performClick()

            assertTrue(capturedIntents.contains(VerifyEmailPageIntent.GoToDashboard))
        }
}
