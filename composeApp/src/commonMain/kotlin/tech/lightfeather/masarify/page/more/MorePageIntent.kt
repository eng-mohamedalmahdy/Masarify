package tech.lightfeather.masarify.page.more

import tech.lightfeather.domain.model.AppLanguage
import tech.lightfeather.domain.model.ImportMode

internal sealed interface MorePageIntent {
    data object LoadData : MorePageIntent

    sealed interface NavigationIntent : MorePageIntent {
        data object SelectCurrencyManagementDetail : NavigationIntent

        data object SelectPrivacyPolicyDetail : NavigationIntent

        data object SelectContactUsDetail : NavigationIntent

        data object SelectRateUsDetail : NavigationIntent

        data object SelectCategoryManagementDetail : NavigationIntent

        data object SelectBackupRestoreDetail : NavigationIntent

        data object SelectNotificationSettingsDetail : NavigationIntent
    }

    data class ToggleDarkTheme(
        val enabled: Boolean,
    ) : MorePageIntent

    data class ToggleBiometric(
        val enabled: Boolean,
    ) : MorePageIntent

    data class ToggleAutoSyncRates(
        val enabled: Boolean,
    ) : MorePageIntent

    data class ToggleAutoSyncData(
        val enabled: Boolean,
    ) : MorePageIntent

    data object SyncNow : MorePageIntent

    data class SelectLanguage(
        val language: AppLanguage,
    ) : MorePageIntent

    data object ClearNavigation : MorePageIntent

    data object ExportData : MorePageIntent

    class ImportData(
        val bytes: ByteArray,
        val mode: ImportMode,
    ) : MorePageIntent

    data object Logout : MorePageIntent

    data object ShowLogoutAllDialog : MorePageIntent

    data object DismissLogoutAllDialog : MorePageIntent

    data object LogoutAllDevices : MorePageIntent

    data object ResendVerification : MorePageIntent

    data object NavigateToSignIn : MorePageIntent

    data object ToggleFailedExpanded : MorePageIntent

    data class RetrySyncEntry(
        val id: Long,
    ) : MorePageIntent

    data object RetryAllFailed : MorePageIntent

    data class DeleteFailedEntry(
        val id: Long,
    ) : MorePageIntent

    data object DeleteAllFailed : MorePageIntent

    data object NavigateToPaywall : MorePageIntent
}
