package com.lightfeather.masarify.page.more

import com.lightfeather.domain.model.AppLanguage

internal data class MorePageState(
    val isDarkTheme: Boolean = false,
    val selectedLanguage: AppLanguage = AppLanguage.English,
    val availableLanguages: List<AppLanguage> = listOf(AppLanguage.English, AppLanguage.Arabic),
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
