package tech.lightfeather.masarify.page.auth.resetpassword

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ResetPasswordPageContentTest {
    @Test
    fun bothPasswordFieldsDisplayedInInitialState() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    ResetPasswordPageContent(
                        token = "test-token",
                        state = ResetPasswordPageState(),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("reset_password_new_password_field").assertIsDisplayed()
            onNodeWithTag("reset_password_confirm_password_field").assertIsDisplayed()
        }

    @Test
    fun submitButtonDisplayedWhenNotLoading() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    ResetPasswordPageContent(
                        token = "test-token",
                        state = ResetPasswordPageState(isLoading = false),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("reset_password_submit_button").assertIsDisplayed()
        }

    @Test
    fun submitButtonHiddenWhenLoading() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    ResetPasswordPageContent(
                        token = "test-token",
                        state = ResetPasswordPageState(isLoading = true),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("reset_password_submit_button").assertDoesNotExist()
        }

    @Test
    fun typingInNewPasswordFieldFiresUpdateNewPasswordIntent() =
        runComposeUiTest {
            val capturedIntents = mutableListOf<ResetPasswordPageIntent>()
            setContent {
                AppTheme(useDarkTheme = false) {
                    ResetPasswordPageContent(
                        token = "test-token",
                        state = ResetPasswordPageState(),
                        onIntent = capturedIntents::add,
                    )
                }
            }

            onNodeWithTag("reset_password_new_password_field").performTextInput("newpass")

            assertTrue(capturedIntents.filterIsInstance<ResetPasswordPageIntent.UpdateNewPassword>().isNotEmpty())
        }
}
