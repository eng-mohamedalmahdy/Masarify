package tech.lightfeather.masarify.page.bankaccounts

import tech.lightfeather.designsystem.model.UiAttachment
import tech.lightfeather.designsystem.model.UiBankAccount
import tech.lightfeather.designsystem.model.UiCategory
import tech.lightfeather.designsystem.model.UiCurrency
import tech.lightfeather.designsystem.model.UiTransactionDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal data class BankAccountsPageState(
    val bankAccounts: Flow<List<UiBankAccount>>,
    val userAccountsCurrencies: Flow<List<UiCurrency>> = flowOf(),
    val defaultCurrency: Flow<UiCurrency?> = flowOf(),
    val selectedAccount: UiBankAccount? = null,
    val selectedCurrency: UiCurrency? = null,
    val totalAmountInSelectedOrDefaultCurrency: String = "",
    val showAddEditDialog: Boolean = false,
    val underProcessTransaction: UiTransactionDetails? = null,
    val categories: List<UiCategory> = emptyList(),
    // Attachment state
    val selectedAttachments: List<UiAttachment> = emptyList(),
    val transactionAttachments: Map<String, List<UiAttachment>> = emptyMap(),
)
