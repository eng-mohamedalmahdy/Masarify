package tech.lightfeather.masarify.page.more

import tech.lightfeather.domain.model.AppLanguage
import tech.lightfeather.domain.model.AppLanguages
import tech.lightfeather.domain.model.SubscriptionPlan
import tech.lightfeather.domain.model.sync.SyncQueueEntry

internal data class MorePageState(
    val isDarkTheme: Boolean = false,
    val isProActive: Boolean = false,
    val currentPlan: SubscriptionPlan = SubscriptionPlan.FREE,
    val isBiometricEnabled: Boolean = false,
    val isAutoSyncRatesEnabled: Boolean = true,
    val isAutoSyncDataEnabled: Boolean = true,
    val isAuthenticated: Boolean = false,
    val selectedLanguage: AppLanguage = AppLanguages.English,
    val availableLanguages: List<AppLanguage> = listOf(AppLanguages.English, AppLanguages.Arabic),
    val selectedDetailItem: MoreDetailItem? = null,
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val isSyncing: Boolean = false,
    val failedSyncCount: Int = 0,
    val failedEntries: List<SyncQueueEntry> = emptyList(),
    val isFailedExpanded: Boolean = false,
    val isLogoutAllDialogVisible: Boolean = false,
    val isEmailVerified: Boolean = true,
)

internal sealed class MoreDetailItem {
    data object CurrencyManagement : MoreDetailItem()

    data object PrivacyPolicy : MoreDetailItem()

    data object ContactUs : MoreDetailItem()

    data object RateUs : MoreDetailItem()

    data object CategoryManagement : MoreDetailItem()

    data object BackupRestore : MoreDetailItem()

    data object NotificationSettings : MoreDetailItem()
}
