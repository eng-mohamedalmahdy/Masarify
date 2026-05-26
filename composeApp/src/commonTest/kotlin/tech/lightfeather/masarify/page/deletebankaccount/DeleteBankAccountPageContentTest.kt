package tech.lightfeather.masarify.page.deletebankaccount

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DeleteBankAccountPageContentTest {
    @Test
    fun confirmButtonIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    DeleteBankAccountPageContent(
                        toBeDeletedBankAccount = UiBankAccount.dummy,
                        onConfirm = {},
                        onCancel = {},
                    )
                }
            }

            onNodeWithTag("alert_dialog_confirm_button").assertIsDisplayed()
        }

    @Test
    fun dismissButtonIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    DeleteBankAccountPageContent(
                        toBeDeletedBankAccount = UiBankAccount.dummy,
                        onConfirm = {},
                        onCancel = {},
                    )
                }
            }

            onNodeWithTag("alert_dialog_dismiss_button").assertIsDisplayed()
        }

    @Test
    fun clickingConfirmButtonFiresOnConfirm() =
        runComposeUiTest {
            var confirmCalled = false
            setContent {
                AppTheme(useDarkTheme = false) {
                    DeleteBankAccountPageContent(
                        toBeDeletedBankAccount = UiBankAccount.dummy,
                        onConfirm = { confirmCalled = true },
                        onCancel = {},
                    )
                }
            }

            onNodeWithTag("alert_dialog_confirm_button").performClick()

            assertTrue(confirmCalled)
        }

    @Test
    fun clickingDismissButtonFiresOnCancel() =
        runComposeUiTest {
            var cancelCalled = false
            setContent {
                AppTheme(useDarkTheme = false) {
                    DeleteBankAccountPageContent(
                        toBeDeletedBankAccount = UiBankAccount.dummy,
                        onConfirm = {},
                        onCancel = { cancelCalled = true },
                    )
                }
            }

            onNodeWithTag("alert_dialog_dismiss_button").performClick()

            assertTrue(cancelCalled)
        }
}
