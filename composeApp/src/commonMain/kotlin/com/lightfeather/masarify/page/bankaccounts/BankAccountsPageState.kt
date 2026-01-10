package com.lightfeather.masarify.page.bankaccounts

import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiTransaction
import kotlinx.coroutines.flow.Flow

internal data class BankAccountsPageState(
    val bankAccounts: Flow<List<UiBankAccount>>,
    val userAccountsCurrencies: Flow<List<UiCurrency>>,
    val defaultCurrency: Flow<UiCurrency?>,
    val selectedAccount: UiBankAccount? = null,
    val selectedCurrency: UiCurrency? = null,
    val totalAmountInSelectedOrDefaultCurrency: String = "",
    val showAddEditDialog: Boolean = false,
    val underProcessTransaction: UiTransaction? = null,
    val categories: List<UiCategory> = emptyList(),
)
