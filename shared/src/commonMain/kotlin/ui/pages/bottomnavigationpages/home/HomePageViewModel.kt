package ui.pages.bottomnavigationpages.home

import com.lightfeather.core.usecase.GetAllTransactions
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ui.pages.bottomnavigationpages.home.model.UiExpenseModel
import ui.pages.bottomnavigationpages.home.model.toUiExpenseModel

class HomePageViewModel(private val getAllTransactions: GetAllTransactions) : ViewModel() {

    private val _expensesListFlow = MutableStateFlow<List<UiExpenseModel>>(listOf())
    val expensesListFlow = _expensesListFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _expensesListFlow.update {
                getAllTransactions().map { it.toUiExpenseModel() }
            }
        }
    }
}