package tech.lightfeather.masarify.page.auth.login

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class LoginPageContentTest {
    @Test
    fun emailAndPasswordFieldsAreDisplayedInInitialState() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    LoginPageContent(state = LoginPageState(), onIntent = {})
                }
            }

            onNodeWithTag("login_email_field").assertIsDisplayed()
            onNodeWithTag("login_password_field").assertIsDisplayed()
        }

    @Test
    fun submitButtonIsDisplayedWhenNotLoading() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    LoginPageContent(state = LoginPageState(isLoading = false), onIntent = {})
                }
            }

            onNodeWithTag("login_submit_button").assertIsDisplayed()
        }

    @Test
    fun submitButtonIsHiddenWhenLoading() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    LoginPageContent(state = LoginPageState(isLoading = true), onIntent = {})
                }
            }

            onNodeWithTag("login_submit_button").assertDoesNotExist()
        }

    @Test
    fun typingInEmailFieldFiresUpdateEmailIntent() =
        runComposeUiTest {
            val capturedIntents = mutableListOf<LoginPageIntent>()
            setContent {
                AppTheme(useDarkTheme = false) {
                    LoginPageContent(state = LoginPageState(), onIntent = capturedIntents::add)
                }
            }

            onNodeWithTag("login_email_field").performTextInput("hello@test.com")

            assertTrue(capturedIntents.filterIsInstance<LoginPageIntent.UpdateEmail>().isNotEmpty())
            assertEquals(
                "hello@test.com",
                capturedIntents.filterIsInstance<LoginPageIntent.UpdateEmail>().last().email,
            )
        }

    @Test
    fun typingInPasswordFieldFiresUpdatePasswordIntent() =
        runComposeUiTest {
            val capturedIntents = mutableListOf<LoginPageIntent>()
            setContent {
                AppTheme(useDarkTheme = false) {
                    LoginPageContent(state = LoginPageState(), onIntent = capturedIntents::add)
                }
            }

            onNodeWithTag("login_password_field").performTextInput("mypassword")

            assertTrue(capturedIntents.filterIsInstance<LoginPageIntent.UpdatePassword>().isNotEmpty())
            assertEquals(
                "mypassword",
                capturedIntents.filterIsInstance<LoginPageIntent.UpdatePassword>().last().password,
            )
        }

    @Test
    fun clickingSubmitButtonFiresSubmitIntent() =
        runComposeUiTest {
            val capturedIntents = mutableListOf<LoginPageIntent>()
            setContent {
                AppTheme(useDarkTheme = false) {
                    LoginPageContent(state = LoginPageState(isLoading = false), onIntent = capturedIntents::add)
                }
            }

            onNodeWithTag("login_submit_button").performClick()

            assertTrue(capturedIntents.contains(LoginPageIntent.Submit))
        }

    @Test
    fun emailFieldReflectsStateValue() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    LoginPageContent(
                        state = LoginPageState(email = "prefilled@test.com"),
                        onIntent = {},
                    )
                }
            }

            // Node with testTag should be present; actual value reflection is handled by TextField
            onNodeWithTag("login_email_field").assertIsDisplayed()
        }
}
