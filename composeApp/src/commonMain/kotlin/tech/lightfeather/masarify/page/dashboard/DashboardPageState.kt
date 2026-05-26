package tech.lightfeather.masarify.page.dashboard

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import tech.lightfeather.designsystem.model.TransactionListItem
import tech.lightfeather.designsystem.model.UiAttachment
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.model.UiCategory
import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.designsystem.model.UiFinancialSession
import tech.lightfeather.designsystem.model.UiQuickStats
import tech.lightfeather.designsystem.model.UiSpendingAnalytics
import tech.lightfeather.designsystem.model.UiTransaction
import tech.lightfeather.designsystem.model.UiTransactionDetails

/**
 * Dashboard page state containing all data for the main dashboard screen
 *
 * @property userName Current user's name for personalized greeting
 * @property greeting Time-based greeting message (Good Morning, etc.)
 * @property selectedMonth Display text for currently selected month (e.g., "January 2026")
 * @property selectedMonthTimestamp Unix timestamp for the selected month
 * @property selectedCurrency Currently selected currency for displaying totals
 * @property availableCurrencies Flow of all currencies user has accounts in
 * @property totalBalance Total balance across all accounts in selected currency
 * @property income Total income for selected month and currency
 * @property expense Total expenses for selected month and currency
 * @property quickStats Quick stats broken down by account type (cash, debit, credit)
 * @property accounts Flow of all bank accounts
 * @property displayedAccountsLimit Maximum number of accounts to show in summary
 * @property spendingAnalytics Spending vs saving analytics for selected month
 * @property recentTransactions Flow of recent transactions
 * @property displayedTransactionsLimit Maximum number of transactions to show in summary
 * @property isLoading General loading state for page
 * @property isAnalyticsLoading Loading state specific to analytics calculation
 * @property selectedAccount Currently selected account for detail pane navigation
 * @property selectedTransaction Currently selected transaction for extra pane navigation
 * @property transactionAttachments Map of transaction ID to its attachments
 * @property showAddEditDialog Whether to show the add/edit transaction dialog
 * @property underProcessTransaction Transaction being edited or created
 * @property selectedAttachments Attachments selected for the current transaction
 * @property categories Available categories for transaction categorization
 */
internal data class DashboardPageState(
    // User information
    val userName: String = "",
    val greeting: String = "",
    // Month navigation
    val selectedMonth: String = "",
    val selectedMonthTimestamp: Long = 0L,
    // Overview section
    val selectedCurrency: UiCurrency? = null,
    val availableCurrencies: Flow<List<UiCurrency>> = emptyFlow(),
    val totalBalance: String = "",
    val income: String = "",
    val expense: String = "",
    val quickStats: UiQuickStats = UiQuickStats(),
    // Accounts section
    val accounts: Flow<List<UiBankAccount>> = emptyFlow(),
    val displayedAccountsLimit: Int = 3,
    // Spending analytics
    val spendingAnalytics: UiSpendingAnalytics? = null,
    // Recent transactions
    val recentTransactions: Flow<List<UiTransaction>> = emptyFlow(),
    val displayedTransactionsLimit: Int = 5,
    // Recent activity timeline (transactions + start over markers merged)
    val recentTimelineItems: List<TransactionListItem> = emptyList(),
    // Loading states
    val isAnalyticsLoading: Boolean = false,
    // Adaptive navigation state
    val selectedAccount: UiBankAccount? = null,
    val selectedTransaction: UiTransaction? = null,
    // Transaction management
    val transactionAttachments: Map<String, List<UiAttachment>> = emptyMap(),
    val showAddEditDialog: Boolean = false,
    val underProcessTransaction: UiTransactionDetails? = null,
    val selectedAttachments: List<UiAttachment> = emptyList(),
    val categories: List<UiCategory> = emptyList(),
    // Start Over feature
    val showStartOverDialog: Boolean = false,
    val startOverEditingSession: UiFinancialSession? = null,
    // Fix Balance feature
    val showFixBalanceDialog: Boolean = false,
    val fixBalanceAccount: UiBankAccount? = null,
    // Biometric suggestion
    val showBiometricSuggestion: Boolean = false,
    // Engagement: tips + notification banner
    val activeTipId: Int? = null,
    val showNotificationBanner: Boolean = false,
) {
    companion object {
        val dummy =
            DashboardPageState(
                userName = "John Doe",
                greeting = "good_morning",
                selectedMonth = "January 2026",
                selectedMonthTimestamp = 1672531200L,
                selectedCurrency = UiCurrency.dummy,
            )
    }
}
