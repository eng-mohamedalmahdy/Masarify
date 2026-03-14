package com.lightfeather.masarify.page.more

import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.model.AppLanguages

internal data class MorePageState(
    val isDarkTheme: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val selectedLanguage: AppLanguage = AppLanguages.English,
    val availableLanguages: List<AppLanguage> = listOf(AppLanguages.English, AppLanguages.Arabic),
    val selectedDetailItem: MoreDetailItem? = null,
    val isLoading: Boolean = false,
)

internal sealed class MoreDetailItem {
    data object CurrencyManagement : MoreDetailItem()

    data object PrivacyPolicy : MoreDetailItem()

    data object ContactUs : MoreDetailItem()

    data object RateUs : MoreDetailItem()

    data object CategoryManagement : MoreDetailItem()
}
