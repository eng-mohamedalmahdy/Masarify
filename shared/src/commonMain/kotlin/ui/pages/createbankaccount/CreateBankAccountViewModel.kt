package ui.pages.createbankaccount

import com.lightfeather.core.usecase.CreateAccount
import com.lightfeather.core.usecase.GetAllCurrencies
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ui.entity.UiBankAccount
import ui.entity.UiCurrency
import ui.entity.toDomainBankAccount
import ui.entity.toUiCurrency

class CreateBankAccountViewModel(
    private val createBankAccountUseCase: CreateAccount,
    private val getAllCurrenciesUseCase: GetAllCurrencies
) : ViewModel() {

    private val _currenciesFlow = MutableStateFlow<List<UiCurrency>>(listOf())
    val currenciesFlow = _currenciesFlow.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _currenciesFlow.value = getAllCurrenciesUseCase().map { it.toUiCurrency() }
        }
    }

    fun createBankAccount(bankAccount: UiBankAccount) {
        CoroutineScope(Dispatchers.IO).launch {
            createBankAccountUseCase(bankAccount.toDomainBankAccount())
        }
    }


}