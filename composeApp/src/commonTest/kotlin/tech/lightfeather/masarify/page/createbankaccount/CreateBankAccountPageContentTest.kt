package tech.lightfeather.masarify.page.createbankaccount

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CreateBankAccountPageContentTest {
    @Test
    fun nameFieldIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    CreateBankAccountPageContent(
                        state = CreateBankAccountPageState(),
                        onIntent = {},
                        onBack = {},
                    )
                }
            }

            onNodeWithTag("create_account_name_field").assertIsDisplayed()
        }

    @Test
    fun submitButtonIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    CreateBankAccountPageContent(
                        state = CreateBankAccountPageState(),
                        onIntent = {},
                        onBack = {},
                    )
                }
            }

            onNodeWithTag("create_account_submit_button").assertIsDisplayed()
        }
}
