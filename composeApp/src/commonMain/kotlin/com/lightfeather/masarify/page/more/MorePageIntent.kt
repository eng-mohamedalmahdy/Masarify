package com.lightfeather.masarify.page.more

import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.model.ImportMode

internal sealed interface MorePageIntent {
    data object LoadData : MorePageIntent

    sealed interface NavigationIntent : MorePageIntent {
        data object SelectCurrencyManagementDetail : NavigationIntent

        data object SelectPrivacyPolicyDetail : NavigationIntent

        data object SelectContactUsDetail : NavigationIntent

        data object SelectRateUsDetail : NavigationIntent

        data object SelectCategoryManagementDetail : NavigationIntent

        data object SelectBackupRestoreDetail : NavigationIntent
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
}
