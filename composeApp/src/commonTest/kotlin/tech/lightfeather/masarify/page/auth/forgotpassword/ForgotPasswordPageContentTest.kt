package tech.lightfeather.masarify.page.auth.forgotpassword

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ForgotPasswordPageContentTest {
    @Test
    fun emailFieldDisplayedInInitialState() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    ForgotPasswordPageContent(state = ForgotPasswordPageState(), onIntent = {})
                }
            }

            onNodeWithTag("forgot_password_email_field").assertIsDisplayed()
        }

    @Test
    fun submitButtonDisplayedWhenNotLoading() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    ForgotPasswordPageContent(
                        state = ForgotPasswordPageState(isLoading = false, isSent = false),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("forgot_password_submit_button").assertIsDisplayed()
        }

    @Test
    fun submitButtonHiddenWhenLoading() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    ForgotPasswordPageContent(
                        state = ForgotPasswordPageState(isLoading = true),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("forgot_password_submit_button").assertDoesNotExist()
        }

    @Test
    fun successStateShowsBackButtonAndHidesSubmit() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    ForgotPasswordPageContent(
                        state = ForgotPasswordPageState(isSent = true),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("forgot_password_back_button").assertIsDisplayed()
            onNodeWithTag("forgot_password_submit_button").assertDoesNotExist()
        }
}
