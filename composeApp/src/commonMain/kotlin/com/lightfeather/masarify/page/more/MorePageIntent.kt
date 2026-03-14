package com.lightfeather.masarify.page.more

import com.lightfeather.domain.model.AppLanguage

internal sealed interface MorePageIntent {
    data object LoadData : MorePageIntent

    sealed interface NavigationIntent : MorePageIntent {
        data object SelectCurrencyManagementDetail : NavigationIntent

        data object SelectPrivacyPolicyDetail : NavigationIntent

        data object SelectContactUsDetail : NavigationIntent

        data object SelectRateUsDetail : NavigationIntent

        data object SelectCategoryManagementDetail : NavigationIntent
    }

    data class ToggleDarkTheme(
        val enabled: Boolean,
    ) : MorePageIntent

    data class ToggleBiometric(
        val enabled: Boolean,
    ) : MorePageIntent

    data class SelectLanguage(
        val language: AppLanguage,
    ) : MorePageIntent

    data object ClearNavigation : MorePageIntent
}
