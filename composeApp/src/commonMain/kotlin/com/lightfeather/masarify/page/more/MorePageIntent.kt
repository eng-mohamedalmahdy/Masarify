@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.lightfeather.masarify.page.more

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import com.lightfeather.domain.model.AppLanguage

internal sealed interface MorePageIntent {
    data object LoadData : MorePageIntent

    sealed class NavigationIntent(
        open val detailItem: MoreDetailItem?,
        open val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
    ) : MorePageIntent {
        data class SelectCurrencyManagementDetail(
            override val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
        ) : NavigationIntent(
                detailItem = MoreDetailItem.CurrencyManagement,
                navigator = navigator,
            )

        data class SelectPrivacyPolicyDetail(
            override val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
        ) : NavigationIntent(
                detailItem = MoreDetailItem.PrivacyPolicy,
                navigator = navigator,
            )

        data class SelectContactUsDetail(
            override val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
        ) : NavigationIntent(
                detailItem = MoreDetailItem.ContactUs,
                navigator = navigator,
            )

        data class SelectRateUsDetail(
            override val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
        ) : NavigationIntent(
                detailItem = MoreDetailItem.RateUs,
                navigator = navigator,
            )
    }

    data class ToggleDarkTheme(
        val enabled: Boolean,
    ) : MorePageIntent

    data class SelectLanguage(
        val language: AppLanguage,
    ) : MorePageIntent

    data class ClearNavigation(
        val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
    ) : MorePageIntent

    data object NavigateToCategoryManagement : MorePageIntent
}
