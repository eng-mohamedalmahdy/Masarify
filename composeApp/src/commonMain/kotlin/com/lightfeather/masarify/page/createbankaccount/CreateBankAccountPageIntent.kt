package com.lightfeather.masarify.page.createbankaccount

import com.lightfeather.designsystem.model.UiCurrency

sealed interface CreateBankAccountPageIntent {
    data class UpdateName(
        val name: String,
    ) : CreateBankAccountPageIntent

    data class UpdateDescription(
        val description: String,
    ) : CreateBankAccountPageIntent

    data class UpdateInitialBalance(
        val balance: String,
    ) : CreateBankAccountPageIntent

    data class UpdateColor(
        val color: String,
    ) : CreateBankAccountPageIntent

    data class UpdateLogo(
        val logo: String,
    ) : CreateBankAccountPageIntent

    data class UpdateCurrency(
        val currency: UiCurrency,
    ) : CreateBankAccountPageIntent

    data class AddNewCurrency(
        val currency: UiCurrency,
    ) : CreateBankAccountPageIntent

    data object NavigateBack : CreateBankAccountPageIntent

    data object Submit : CreateBankAccountPageIntent

    data class SaveColor(
        val color: String,
    ) : CreateBankAccountPageIntent
}
