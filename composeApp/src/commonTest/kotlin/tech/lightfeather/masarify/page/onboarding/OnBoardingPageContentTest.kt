package tech.lightfeather.masarify.page.onboarding

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class OnBoardingPageContentTest {
    @Test
    fun userNameFieldIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    OnBoardingPageContent(
                        state = OnBoardingPageState(),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("onboarding_user_name_field").assertIsDisplayed()
        }

    @Test
    fun submitButtonIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    OnBoardingPageContent(
                        state = OnBoardingPageState(),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("onboarding_submit_button").assertIsDisplayed()
        }
}
