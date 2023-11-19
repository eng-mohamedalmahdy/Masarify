package ui.pages.bottomnavigationpages.currencies

import com.lightfeather.core.usecase.CreateCurrency
import com.lightfeather.core.usecase.GetAllCurrencies
import com.lightfeather.core.usecase.GetAllCurrenciesExchangeRates
import com.lightfeather.core.usecase.UpdateCurrencyExchangeRates
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.entity.UiCurrency
import ui.entity.toDomainCurrency
import ui.entity.toUiCurrency
import ui.pages.bottomnavigationpages.currencies.model.UiExchangeRate
import ui.pages.bottomnavigationpages.currencies.model.toDomainExchangeRate
import ui.pages.bottomnavigationpages.currencies.model.toUiExchangeRate

class CurrenciesViewModel(
    private val getAllExchangeRate: GetAllCurrenciesExchangeRates,
    private val getAllCurrencies: GetAllCurrencies,
    private val createCurrency: CreateCurrency,
    private val updateCurrencyExchangeRate: UpdateCurrencyExchangeRates
) : ViewModel() {


    private val ioDispatchers = SupervisorJob() + Dispatchers.IO


    private val _currenciesExchangeRateFlow = MutableStateFlow<List<List<UiExchangeRate>>>(listOf())
    val currenciesExchangeRateFlow = _currenciesExchangeRateFlow.asStateFlow()

    private val _currenciesFlow = MutableStateFlow<List<UiCurrency>>(listOf())
    val currenciesFlow = _currenciesFlow.asStateFlow()

    fun loadCurrenciesAndExchangeRates() = viewModelScope.launch(ioDispatchers) {
        val exchangeRates = getAllExchangeRate()
        val currencies = getAllCurrencies()

        _currenciesFlow.update { currencies.map { it.toUiCurrency() } }

        _currenciesExchangeRateFlow.update {
            exchangeRates.map { it.map { it.toUiExchangeRate() } }
        }
    }

    fun saveCurrenciesAndExchangeRates(newExchangeRates: List<List<UiExchangeRate>>) {
        viewModelScope.launch(ioDispatchers) {
            updateCurrencyExchangeRate(newExchangeRates.map { it.map { it.toDomainExchangeRate() } })
        }
    }

    fun addCurrency(currency: UiCurrency) {
        viewModelScope.launch(ioDispatchers) {
            createCurrency(currency.toDomainCurrency())
        }
    }
}