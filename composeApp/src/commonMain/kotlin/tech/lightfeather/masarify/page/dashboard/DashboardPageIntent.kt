package tech.lightfeather.masarify.page.dashboard

import tech.lightfeather.designsystem.component.organisms.dialog.UiTransactionData
import tech.lightfeather.designsystem.model.UiAccountSnapshot
import tech.lightfeather.designsystem.model.UiAttachment
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.designsystem.model.UiFinancialSession
import tech.lightfeather.designsystem.model.UiTransaction
import tech.lightfeather.designsystem.model.UiTransactionDetails

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

    /**
     * Toggle the expandable FAB on the dashboard
     */

    /**
     * Show the Start Over dialog
     */
    data object ShowStartOverDialog : DashboardPageIntent

    /**
     * Confirm the Start Over action with account snapshots
     */
    data class ConfirmStartOver(
        val name: String?,
        val snapshots: List<UiAccountSnapshot>,
    ) : DashboardPageIntent

    /**
     * Dismiss the Start Over dialog
     */
    data object DismissStartOverDialog : DashboardPageIntent

    /**
     * Edit an existing financial session
     */
    data class EditStartOverSession(
        val session: UiFinancialSession,
    ) : DashboardPageIntent

    /**
     * Delete a financial session
     */
    data class DeleteStartOverSession(
        val sessionId: String,
    ) : DashboardPageIntent

    /**
     * Toggle a Start Over marker expansion in the timeline
     */
    data class ToggleStartOverMarker(
        val sessionId: String,
    ) : DashboardPageIntent

    /**
     * Show the Fix Balance dialog (pre-select account if provided)
     */
    data class ShowFixBalanceDialog(
        val account: UiBankAccount? = null,
    ) : DashboardPageIntent

    /**
     * Dismiss the Fix Balance dialog
     */
    data object DismissFixBalanceDialog : DashboardPageIntent

    /**
     * Apply a balance adjustment transaction
     */
    data class ApplyBalanceAdjustment(
        val account: UiBankAccount,
        val actualBalance: Double,
        val isIncrease: Boolean,
    ) : DashboardPageIntent

    /**
     * Check if biometric suggestion should be shown (pass availability from UI)
     */
    data class CheckBiometricSuggestion(
        val isAvailable: Boolean,
    ) : DashboardPageIntent

    /**
     * Dismiss the biometric suggestion dialog without enabling
     */
    data object DismissBiometricSuggestion : DashboardPageIntent

    /**
     * Enable biometric from the suggestion dialog and dismiss
     */
    data object EnableBiometricFromSuggestion : DashboardPageIntent

    /**
     * Toggle inline expand/collapse for a transaction in the timeline
     *
     * @property transactionId The ID of the transaction to toggle
     */
    data class ToggleTransactionExpansion(
        val transactionId: String,
    ) : DashboardPageIntent

    /**
     * Delete a transaction directly by ID (from expanded quick action)
     *
     * @property transactionId The ID of the transaction to delete
     */
    data class DeleteTransactionById(
        val transactionId: String,
    ) : DashboardPageIntent

    data class DismissTip(
        val tipId: Int,
    ) : DashboardPageIntent

    data object DismissNotificationBanner : DashboardPageIntent

    data object EnableNotificationsFromBanner : DashboardPageIntent
}
