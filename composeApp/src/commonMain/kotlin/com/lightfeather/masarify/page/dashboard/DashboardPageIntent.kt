package com.lightfeather.masarify.page.dashboard

import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiTransaction

/**
 * Dashboard page intents representing all user actions on the dashboard
 */
internal sealed interface DashboardPageIntent {
    /**
     * Load initial dashboard data (accounts, transactions, user info, etc.)
     */
    data object LoadData : DashboardPageIntent

    /**
     * Navigate to next month in the overview section
     */
    data object NextMonth : DashboardPageIntent

    /**
     * Navigate to previous month in the overview section
     */
    data object PreviousMonth : DashboardPageIntent

    /**
     * Select a different currency for displaying total balance
     *
     * @property currency The currency to switch to
     */
    data class SelectCurrency(
        val currency: UiCurrency,
    ) : DashboardPageIntent

    /**
     * Navigate to the full accounts page
     */
    data object NavigateToAccounts : DashboardPageIntent

    /**
     * Navigate to the full transactions page
     */
    data object NavigateToTransactions : DashboardPageIntent

    /**
     * Navigate to add new account page
     */
    data object NavigateToAddAccount : DashboardPageIntent

    /**
     * Navigate to a specific bank account detail page
     *
     * @property account The account to view
     */
    data class NavigateToAccount(
        val account: UiBankAccount,
    ) : DashboardPageIntent

    /**
     * Navigate to a specific transaction detail page
     *
     * @property transaction The transaction to view
     */
    data class NavigateToTransaction(
        val transaction: UiTransaction,
    ) : DashboardPageIntent
}
