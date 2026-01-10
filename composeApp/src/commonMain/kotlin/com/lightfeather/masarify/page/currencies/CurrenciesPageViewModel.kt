package com.lightfeather.masarify.page.currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.UiCurrencyExchangeRate
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.usecase.CreateCurrency
import com.lightfeather.domain.usecase.DeleteCurrency
import com.lightfeather.domain.usecase.GetAllCurrencies
import com.lightfeather.domain.usecase.GetExchangeRatesOfCurrency
import com.lightfeather.domain.usecase.UpdateCurrency
import com.lightfeather.domain.usecase.UpdateCurrencyExchangeRates
import com.lightfeather.masarify.mappers.toCurrency
import com.lightfeather.masarify.mappers.toCurrencyExchangeRate
import com.lightfeather.masarify.mappers.toUiCurrency
import com.lightfeather.masarify.mappers.toUiCurrencyExchangeRate
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CurrenciesPageViewModel(
    private val getAllCurrencies: GetAllCurrencies,
    private val createCurrency: CreateCurrency,
    private val updateCurrency: UpdateCurrency,
    private val deleteCurrency: DeleteCurrency,
    private val getExchangeRatesOfCurrency: GetExchangeRatesOfCurrency,
    private val updateCurrencyExchangeRates: UpdateCurrencyExchangeRates,
) : ViewModel() {
    private val _state = MutableStateFlow(CurrenciesPageState())
    internal val state: StateFlow<CurrenciesPageState> = _state

    private val editedRates = mutableMapOf<String, UiCurrencyExchangeRate>()

    internal fun onIntent(intent: CurrenciesPageIntent) {
        when (intent) {
            is CurrenciesPageIntent.LoadData -> loadData()
            is CurrenciesPageIntent.SelectBaseCurrency -> selectBaseCurrency(intent.currency)
            is CurrenciesPageIntent.ToggleEditMode -> toggleEditMode(intent.enabled)
            is CurrenciesPageIntent.UpdateExchangeRate ->
                updateExchangeRate(
                    intent.exchangeRate,
                    intent.newRate,
                    intent.updateInverse,
                )

            is CurrenciesPageIntent.SaveExchangeRates -> saveExchangeRates(intent.rates)
            is CurrenciesPageIntent.ShowAddCurrencyDialog -> showAddCurrencyDialog(intent.show)
            is CurrenciesPageIntent.ShowEditCurrencyDialog -> showEditCurrencyDialog(intent.currency)
            is CurrenciesPageIntent.CreateCurrency -> createCurrency(intent.name, intent.symbol)
            is CurrenciesPageIntent.UpdateCurrency -> updateCurrency(intent.currency, intent.name, intent.symbol)
            is CurrenciesPageIntent.ShowDeleteDialog -> showDeleteDialog(intent.currency)
            is CurrenciesPageIntent.DeleteCurrency -> deleteCurrency(intent.currency)
            is CurrenciesPageIntent.HideDialogs -> hideDialogs()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val currenciesFlow =
                getAllCurrencies()
                    .foldResult(
                        onSuccess = { currenciesFlow -> currenciesFlow.map { it.map { it.toUiCurrency() } } },
                        onFailure = { flowOf(emptyList()) },
                    )

            _state.value = _state.value.copy(allCurrencies = currenciesFlow)

            // Load first currency as base and its exchange rates
            currenciesFlow.collect { currencies ->
                val firstCurrency = currencies.firstOrNull()
                if (firstCurrency != null) {
                    _state.value = _state.value.copy(baseCurrency = firstCurrency)
                    loadExchangeRatesForBaseCurrency(firstCurrency)
                }
            }
        }
    }

    private fun selectBaseCurrency(currency: UiCurrency) {
        _state.value = _state.value.copy(baseCurrency = currency)
        loadExchangeRatesForBaseCurrency(currency)
    }

    private fun loadExchangeRatesForBaseCurrency(currency: UiCurrency) {
        viewModelScope.launch {
            val domainCurrency = currency.toCurrency()
            _state.value =
                _state.value.copy(
                    exchangeRates =
                        getExchangeRatesOfCurrency(domainCurrency).foldResult(
                            onSuccess = { ratesFlow ->
                                ratesFlow.map { rates ->
                                    rates
                                        .also { Napier.d { "Loaded rates: $it" } }
                                        .map { it.toUiCurrencyExchangeRate() }
                                }
                            },
                            onFailure = { emptyFlow() },
                        ),
                )
        }
    }

    private fun toggleEditMode(enabled: Boolean) {
        if (!enabled) {
            editedRates.clear()
        }
        _state.value = _state.value.copy(isEditMode = enabled)
    }

    private fun updateExchangeRate(
        exchangeRate: UiCurrencyExchangeRate,
        newRate: Double,
        updateInverse: Boolean,
    ) {
        val updatedRate = exchangeRate.copy(rate = newRate)
        editedRates[updatedRate.id] = updatedRate

        if (updateInverse) {
            val inverseId = "${exchangeRate.toCurrency.id}_${exchangeRate.fromCurrency.id}"
            val inverseRate =
                UiCurrencyExchangeRate(
                    id = inverseId,
                    fromCurrency = exchangeRate.toCurrency,
                    toCurrency = exchangeRate.fromCurrency,
                    rate = updatedRate.inverseRate,
                )
            editedRates[inverseId] = inverseRate
        }
    }

    private fun saveExchangeRates(rates: List<UiCurrencyExchangeRate>) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val domainRates = rates.map { it.toCurrencyExchangeRate() }
            val groupedRates = listOf(domainRates)

            updateCurrencyExchangeRates(groupedRates).foldResult(
                onSuccess = {
                    SnackbarService.sendSuccessMessage(MR.strings.exchange_rates_updated)
                    editedRates.clear()
                    _state.value = _state.value.copy(isEditMode = false, isLoading = false)
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.exchange_rates_update_failed)
                    _state.value = _state.value.copy(isLoading = false)
                },
            )
        }
    }

    private fun showAddCurrencyDialog(show: Boolean) {
        _state.value =
            _state.value.copy(
                showAddEditDialog = show,
                editingCurrency = null,
            )
    }

    private fun showEditCurrencyDialog(currency: UiCurrency) {
        _state.value =
            _state.value.copy(
                showAddEditDialog = true,
                editingCurrency = currency,
            )
    }

    private fun createCurrency(
        name: String,
        symbol: String,
    ) {
        viewModelScope.launch {
            if (name.isBlank()) {
                SnackbarService.sendErrorMessage(MR.strings.currency_name_required)
                return@launch
            }
            if (symbol.isBlank()) {
                SnackbarService.sendErrorMessage(MR.strings.currency_symbol_required)
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true)

            val newCurrency = Currency(name = name, sign = symbol)
            createCurrency.invoke(newCurrency).foldResult(
                onSuccess = {
                    SnackbarService.sendSuccessMessage(MR.strings.currency_created_success)
                    hideDialogs()
                    _state.value = _state.value.copy(isLoading = false)
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.currency_create_failed)
                    _state.value = _state.value.copy(isLoading = false)
                },
            )
        }
    }

    private fun updateCurrency(
        currency: UiCurrency,
        name: String,
        symbol: String,
    ) {
        viewModelScope.launch {
            if (name.isBlank()) {
                SnackbarService.sendErrorMessage(MR.strings.currency_name_required)
                return@launch
            }
            if (symbol.isBlank()) {
                SnackbarService.sendErrorMessage(MR.strings.currency_symbol_required)
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true)

            val updatedCurrency = currency.toCurrency().copy(name = name, sign = symbol)
            updateCurrency.invoke(updatedCurrency).foldResult(
                onSuccess = {
                    SnackbarService.sendSuccessMessage(MR.strings.currency_updated_success)
                    hideDialogs()
                    _state.value = _state.value.copy(isLoading = false)
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.currency_update_failed)
                    _state.value = _state.value.copy(isLoading = false)
                },
            )
        }
    }

    private fun showDeleteDialog(currency: UiCurrency) {
        _state.value =
            _state.value.copy(
                showDeleteDialog = true,
                deletingCurrency = currency,
            )
    }

    private fun deleteCurrency(currency: UiCurrency) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            deleteCurrency.invoke(currency.toCurrency()).foldResult(
                onSuccess = {
                    SnackbarService.sendSuccessMessage(MR.strings.currency_deleted_success)
                    hideDialogs()
                    _state.value = _state.value.copy(isLoading = false)
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.currency_delete_failed)
                    _state.value = _state.value.copy(isLoading = false)
                },
            )
        }
    }

    private fun hideDialogs() {
        _state.value =
            _state.value.copy(
                showAddEditDialog = false,
                showDeleteDialog = false,
                editingCurrency = null,
                deletingCurrency = null,
            )
    }
}
