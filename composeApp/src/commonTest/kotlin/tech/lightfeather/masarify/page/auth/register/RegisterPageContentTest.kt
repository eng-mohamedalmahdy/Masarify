package tech.lightfeather.masarify.page.auth.register

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
class RegisterPageContentTest {
    @Test
    fun allFieldsDisplayedInInitialState() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    RegisterPageContent(state = RegisterPageState(), onIntent = {})
                }
            }

            onNodeWithTag("register_name_field").assertIsDisplayed()
            onNodeWithTag("register_email_field").assertIsDisplayed()
            onNodeWithTag("register_password_field").assertIsDisplayed()
            onNodeWithTag("register_confirm_password_field").assertIsDisplayed()
        }

    @Test
    fun submitButtonDisplayedWhenNotLoading() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    RegisterPageContent(state = RegisterPageState(isLoading = false), onIntent = {})
                }
            }

            onNodeWithTag("register_submit_button").assertIsDisplayed()
        }

    @Test
    fun submitButtonHiddenWhenLoading() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    RegisterPageContent(state = RegisterPageState(isLoading = true), onIntent = {})
                }
            }

            onNodeWithTag("register_submit_button").assertDoesNotExist()
        }

    @Test
    fun typingInNameFieldFiresUpdateNameIntent() =
        runComposeUiTest {
            val capturedIntents = mutableListOf<RegisterPageIntent>()
            setContent {
                AppTheme(useDarkTheme = false) {
                    RegisterPageContent(state = RegisterPageState(), onIntent = capturedIntents::add)
                }
            }

            onNodeWithTag("register_name_field").performTextInput("Alice")

            assertTrue(capturedIntents.filterIsInstance<RegisterPageIntent.UpdateName>().isNotEmpty())
            assertEquals(
                "Alice",
                capturedIntents.filterIsInstance<RegisterPageIntent.UpdateName>().last().name,
            )
        }

    @Test
    fun clickingSubmitButtonFiresSubmitIntent() =
        runComposeUiTest {
            val capturedIntents = mutableListOf<RegisterPageIntent>()
            setContent {
                AppTheme(useDarkTheme = false) {
                    RegisterPageContent(state = RegisterPageState(isLoading = false), onIntent = capturedIntents::add)
                }
            }

            onNodeWithTag("register_submit_button").performClick()

            assertTrue(capturedIntents.contains(RegisterPageIntent.Submit))
        }
}
