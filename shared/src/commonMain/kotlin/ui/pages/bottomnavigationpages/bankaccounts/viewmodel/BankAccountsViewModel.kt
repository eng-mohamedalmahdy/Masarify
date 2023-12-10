package ui.pages.bottomnavigationpages.bankaccounts.viewmodel

import com.lightfeather.core.usecase.GetAllAccounts
import com.lightfeather.masarify.MR.strings.accounts
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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
            getAllAccounts().map { accountsList -> UiState.SUCCESS(accountsList.map { it.toUiBankAccount() }) }
                .collect(_bankAccountsFlow)
        }
    }
}