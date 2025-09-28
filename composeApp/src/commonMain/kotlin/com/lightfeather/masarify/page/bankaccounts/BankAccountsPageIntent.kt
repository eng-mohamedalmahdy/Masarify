@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.lightfeather.masarify.page.bankaccounts

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency

internal sealed interface BankAccountsPageIntent {
    data object LoadData : BankAccountsPageIntent

    sealed class NavigationIntent(
        open val bankAccount: UiBankAccount?,
        open val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
    ) : BankAccountsPageIntent {
        data class AddBankAccount(
            override val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
        ) : NavigationIntent(
                navigator = navigator,
                bankAccount = null,
            )

        data class UpdateBankAccount(
            val account: UiBankAccount,
            override val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
        ) : NavigationIntent(
                bankAccount = account,
                navigator = navigator,
            )

        data class SelectAccount(
            val account: UiBankAccount,
            override val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
        ) : NavigationIntent(
                bankAccount = account,
                navigator = navigator,
            )
    }

    data class DeleteBankAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class CreateTransactionInAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class TransferFromAccount(
        val account: UiBankAccount,
    ) : BankAccountsPageIntent

    data class SelectCurrency(
        val currency: UiCurrency?,
    ) : BankAccountsPageIntent

    data class ClearNavigation(
        val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
    ) : BankAccountsPageIntent
}
