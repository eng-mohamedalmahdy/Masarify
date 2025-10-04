package com.lightfeather.masarify.page.currencies

import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiCurrencyExchangeRate

internal sealed interface CurrenciesPageIntent {
    data object LoadData : CurrenciesPageIntent

    data class SelectBaseCurrency(
        val currency: UiCurrency,
    ) : CurrenciesPageIntent

    data class ToggleEditMode(
        val enabled: Boolean,
    ) : CurrenciesPageIntent

    data class UpdateExchangeRate(
        val exchangeRate: UiCurrencyExchangeRate,
        val newRate: Double,
        val updateInverse: Boolean = false,
    ) : CurrenciesPageIntent

    data class SaveExchangeRates(
        val rates: List<UiCurrencyExchangeRate>,
    ) : CurrenciesPageIntent

    data class ShowAddCurrencyDialog(
        val show: Boolean,
    ) : CurrenciesPageIntent

    data class ShowEditCurrencyDialog(
        val currency: UiCurrency,
    ) : CurrenciesPageIntent

    data class CreateCurrency(
        val name: String,
        val symbol: String,
    ) : CurrenciesPageIntent

    data class UpdateCurrency(
        val currency: UiCurrency,
        val name: String,
        val symbol: String,
    ) : CurrenciesPageIntent

    data class ShowDeleteDialog(
        val currency: UiCurrency,
    ) : CurrenciesPageIntent

    data class DeleteCurrency(
        val currency: UiCurrency,
    ) : CurrenciesPageIntent

    data object HideDialogs : CurrenciesPageIntent
}
