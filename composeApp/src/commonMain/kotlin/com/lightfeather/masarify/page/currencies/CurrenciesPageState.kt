package com.lightfeather.masarify.page.currencies

import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiCurrencyExchangeRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal data class CurrenciesPageState(
    val allCurrencies: Flow<List<UiCurrency>> = emptyFlow(),
    val baseCurrency: UiCurrency? = null,
    val exchangeRates: Flow<List<UiCurrencyExchangeRate>> = emptyFlow(),
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val showAddEditDialog: Boolean = false,
    val editingCurrency: UiCurrency? = null,
    val showDeleteDialog: Boolean = false,
    val deletingCurrency: UiCurrency? = null,
)
