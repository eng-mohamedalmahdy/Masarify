package ui.pages.bottomnavigationpages.bankaccounts.viewmodel

import com.lightfeather.core.usecase.GetAllAccounts
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.entity.UiBankAccount
import ui.entity.UiState
import ui.entity.toUiBankAccount

class BankAccountsViewModel(getAllAccounts: GetAllAccounts) : ViewModel() {
    private val _bankAccountsFlow = MutableStateFlow<UiState<List<UiBankAccount>>>(UiState.IDLE())
    val bankAccountsFlow = _bankAccountsFlow.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _bankAccountsFlow.emit(UiState.LOADING())
            _bankAccountsFlow.update {
                UiState.SUCCESS(getAllAccounts().map { it.toUiBankAccount() })
            }
        }
    }
}