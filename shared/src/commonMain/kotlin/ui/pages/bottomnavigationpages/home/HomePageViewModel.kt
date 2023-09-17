package ui.pages.bottomnavigationpages.home

import com.lightfeather.core.usecase.GetAllTransactions
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import ext.formatTimeStampToDate
import ext.formatTimeStampToTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.pages.bottomnavigationpages.home.model.UiExpenseModel
import ui.pages.bottomnavigationpages.home.model.toUiExpenseModel

class HomePageViewModel(private val getAllTransactions: GetAllTransactions) : ViewModel() {

    private val _expensesWithDateListFlow = MutableStateFlow<Map<String, List<UiExpenseModel>>>(mapOf())
    val expensesWithDateListFlow = _expensesWithDateListFlow.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            _expensesWithDateListFlow.update {
                getAllTransactions()
                    .groupBy { it.timestamp.formatTimeStampToDate() }
                    .map { (date, list) -> date to list.map { it.toUiExpenseModel() } }
                    .toMap()
            }
        }
    }
}