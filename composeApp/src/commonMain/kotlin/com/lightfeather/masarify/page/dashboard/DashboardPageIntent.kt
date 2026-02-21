package com.lightfeather.masarify.page.dashboard

import com.lightfeather.designsystem.component.organisms.dialog.UiTransactionData
import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionDetails

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
     * Navigate to a specific bank account detail page (adaptive detail pane)
     *
     * @property account The account to view
     */
    data class NavigateToAccount(
        val account: UiBankAccount,
    ) : DashboardPageIntent

    /**
     * Navigate to a specific transaction detail page (adaptive extra pane)
     *
     * @property transaction The transaction to view
     */
    data class NavigateToTransaction(
        val transaction: UiTransaction,
    ) : DashboardPageIntent

    /**
     * Select an account for detail pane display
     *
     * @property account The account that was selected
     */
    data class SelectAccount(
        val account: UiBankAccount?,
    ) : DashboardPageIntent

    /**
     * Create a new transaction in a specific account
     *
     * @property account The account to create transaction in
     */
    data class CreateTransactionInAccount(
        val account: UiBankAccount,
    ) : DashboardPageIntent

    /**
     * Update an existing transaction
     *
     * @property transaction The transaction to update
     */
    data class UpdateTransaction(
        val transaction: UiTransactionDetails,
    ) : DashboardPageIntent

    /**
     * Delete a transaction
     *
     * @property transaction The transaction to delete
     */
    data class DeleteTransaction(
        val transaction: UiTransactionDetails,
    ) : DashboardPageIntent

    /**
     * Duplicate a transaction
     *
     * @property transaction The transaction to duplicate
     */
    data class DuplicateTransaction(
        val transaction: UiTransactionDetails,
    ) : DashboardPageIntent

    /**
     * Confirm the transaction update/creation
     *
     * @property transaction The transaction data to save
     */
    data class ConfirmUpdateTransaction(
        val transaction: UiTransactionData,
    ) : DashboardPageIntent

    /**
     * Cancel the transaction update/creation
     */
    data object CancelUpdateTransaction : DashboardPageIntent

    /**
     * Pick images for transaction attachments
     */
    data object PickImages : DashboardPageIntent

    /**
     * Delete an attachment from the transaction
     *
     * @property attachment The attachment to delete
     */
    data class DeleteAttachment(
        val attachment: UiAttachment,
    ) : DashboardPageIntent
}
