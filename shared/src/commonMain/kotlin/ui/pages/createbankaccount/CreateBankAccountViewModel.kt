package ui.pages.createbankaccount

import com.lightfeather.core.usecase.CreateAccount
import com.lightfeather.core.usecase.CreateCurrency
import com.lightfeather.core.usecase.GetAllCurrencies
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ui.entity.UiBankAccount
import ui.entity.UiCurrency
import ui.entity.toDomainBankAccount
import ui.entity.toDomainCurrency
import ui.entity.toUiCurrency

class CreateBankAccountViewModel(
    private val createBankAccountUseCase: CreateAccount,
    private val createCurrencyUseCase: CreateCurrency,
    private val getAllCurrenciesUseCase: GetAllCurrencies
) : ViewModel() {

    private val _currenciesFlow = MutableStateFlow<List<UiCurrency>>(listOf())
    val currenciesFlow = _currenciesFlow.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getAllCurrenciesUseCase().map { it.map { it.toUiCurrency() } }.collect(_currenciesFlow)
        }
    }

    fun createBankAccount(bankAccount: UiBankAccount) {
        CoroutineScope(Dispatchers.IO).launch {
            createBankAccountUseCase(bankAccount.toDomainBankAccount())
        }
    }

    fun createBankAccountAndCurrency(uiBankAccount: UiBankAccount) {
        CoroutineScope(Dispatchers.IO).launch {
            val currencyId = createCurrencyUseCase(uiBankAccount.currency.toDomainCurrency())
            createBankAccountUseCase(uiBankAccount.copy(currency = uiBankAccount.currency.copy(id = currencyId)).toDomainBankAccount())
        }
    }
}