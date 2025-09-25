package com.lightfeather.masarify.page.bankaccounts

import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.designsystem.model.UiCurrency
import kotlinx.coroutines.flow.Flow

internal data class BankAccountsPageState(
    val bankAccounts: Flow<List<UiBankAccount>>,
    val userAccountsCurrencies: Flow<List<UiCurrency>>,
    val defaultCurrency: Flow<UiCurrency?>,
    val selectedAccount: UiBankAccount? = null,
    val selectedCurrency: UiCurrency? = null,
    val totalAmountInSelectedOrDefaultCurrency: String = "",
)
