package ui.pages.bottomnavigationpages.home

import com.lightfeather.core.usecase.GetAllTransactions
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import ext.formatTimeStampToDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.entity.UiState
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.home.model.toUiExpenseModel

class HomePageViewModel(
    private val getAllTransactions: GetAllTransactions,
) : ViewModel() {


    private val _expensesWithDateListFlow =
        MutableStateFlow<UiState<Map<String, List<UiTransactionModel>>>>(UiState.IDLE())
    val expensesWithDateListFlow = _expensesWithDateListFlow.asStateFlow()

    init {
        loadAllTransactions()
    }

    private fun loadAllTransactions() {
        CoroutineScope(Dispatchers.Default).launch {
            _expensesWithDateListFlow.emit(UiState.LOADING())
            _expensesWithDateListFlow.update {
                UiState.SUCCESS(getAllTransactions()
                    .groupBy { it.timestamp.formatTimeStampToDate() }
                    .map { (date, list) -> date to list.map { it.toUiExpenseModel() } }
                    .toMap()
                )
            }
        }
    }

    fun filterTransactions(filterKey: String) {
        if (filterKey.isEmpty()) {
            loadAllTransactions()
        } else {
            CoroutineScope(Dispatchers.Default).launch {
                _expensesWithDateListFlow.emit(UiState.LOADING())
                _expensesWithDateListFlow.update {

                    UiState.SUCCESS(getAllTransactions()
                        .filter {
                            it.name.contains(filterKey, true) || it.description?.contains(
                                filterKey,
                                true
                            ) == true
                        }
                        .groupBy { it.timestamp.formatTimeStampToDate() }
                        .map { (date, list) -> date to list.map { it.toUiExpenseModel() } }
                        .toMap()
                    )
                }
            }
        }
    }
}