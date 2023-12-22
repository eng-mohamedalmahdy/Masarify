package ui.pages.bottomnavigationpages.home

import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.usecase.DeleteTransaction
import com.lightfeather.core.usecase.GetAllAccounts
import com.lightfeather.core.usecase.GetAllTransactions
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import ext.formatTimeStampToDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ui.entity.UiBankAccount
import ui.entity.UiState
import ui.entity.toUiBankAccount
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.home.model.toDomainTransaction
import ui.pages.bottomnavigationpages.home.model.toUiTransactionModel

class HomePageViewModel(
    private val getAllTransactions: GetAllTransactions,
    private val getAllBankAccounts: GetAllAccounts,
    private val deleteExpenseUseCase: DeleteTransaction<Transaction.Expense>,
    private val deleteIncomeUseCase: DeleteTransaction<Transaction.Income>,
    private val deleteTransferUseCase: DeleteTransaction<Transaction.Transfer>
) : ViewModel() {


    private val _expensesWithDateListFlow =
        MutableStateFlow<UiState<Map<String, List<UiTransactionModel>>>>(UiState.IDLE())
    val expensesWithDateListFlow = _expensesWithDateListFlow.asStateFlow()

    private val _bankAccountsListFlow = MutableStateFlow<List<UiBankAccount>>(listOf())
    val bankAccountsListFlow = _bankAccountsListFlow.asStateFlow()

    init {
        loadBankAccounts()
        loadAllTransactions()
    }

    private fun loadBankAccounts() {
        CoroutineScope(Dispatchers.IO).launch {
            getAllBankAccounts().map { it.map { it.toUiBankAccount() } }.collect(_bankAccountsListFlow)
        }
    }

    private fun loadAllTransactions() {
        CoroutineScope(Dispatchers.Default).launch {
            _expensesWithDateListFlow.emit(UiState.LOADING())
            getAllTransactions()
                .map {
                    it.groupBy { it.timestamp.formatTimeStampToDate() }
                        .map { (date, list) -> date to list.map { it.toUiTransactionModel() } }
                        .reversed()
                        .toMap()

                }.map { UiState.SUCCESS(it) }.collect(_expensesWithDateListFlow)
        }
    }

    fun filterTransactions(filterKey: String) {
        if (filterKey.isEmpty()) {
            loadAllTransactions()
        } else {
            CoroutineScope(Dispatchers.Default).launch {
                _expensesWithDateListFlow.emit(UiState.LOADING())
                getAllTransactions()
                    .map {
                        it.filter {
                            it.name.contains(filterKey, true) || it.description?.contains(
                                filterKey,
                                true
                            ) == true
                        }.groupBy { it.timestamp.formatTimeStampToDate() }
                            .map { (date, list) -> date to list.map { it.toUiTransactionModel() } }
                            .toMap()
                    }.map { UiState.SUCCESS(it) }.collect(_expensesWithDateListFlow)
            }
        }
    }

    fun deleteTransaction(it: UiTransactionModel) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val transaction = it.toDomainTransaction()) {
                is Transaction.Expense -> deleteExpenseUseCase(transaction)
                is Transaction.Income -> deleteIncomeUseCase(transaction)
                is Transaction.Transfer -> deleteTransferUseCase(transaction)
            }
        }
    }
}