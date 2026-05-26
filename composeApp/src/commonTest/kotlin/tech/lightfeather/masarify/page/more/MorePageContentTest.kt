package tech.lightfeather.masarify.page.more

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.domain.model.AppLanguages
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class MorePageContentTest {
    @Test
    fun darkThemeSwitchIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    MoreListPaneContent(
                        state = MorePageState(selectedLanguage = AppLanguages.English),
                        onDarkThemeToggle = {},
                        onBiometricToggle = {},
                        onAutoSyncRatesToggle = {},
                        onAutoSyncDataToggle = {},
                        onSyncNowClick = {},
                        onToggleFailedExpanded = {},
                        onRetrySyncEntry = {},
                        onRetryAllFailed = {},
                        onDeleteFailedEntry = {},
                        onDeleteAllFailed = {},
                        onSignInClick = {},
                        onLanguageSelected = {},
                        onCurrencyManagementClick = {},
                        onCategoryManagementClick = {},
                        onPrivacyPolicyClick = {},
                        onContactUsClick = {},
                        onRateUsClick = {},
                        onBackupRestoreClick = {},
                        onLogoutClick = {},
                        onLogoutAllClick = {},
                        onResendVerificationClick = {},
                    )
                }
            }

            onNodeWithTag("more_dark_theme_switch").assertIsDisplayed()
        }
}
