package ui.pages.bottomnavigationpages.statistics.viewmodel

import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.usecase.GetAllCurrencies
import com.lightfeather.core.usecase.GetAllTransactions
import com.lightfeather.core.usecase.GetTotalExpenseOfCurrency
import com.lightfeather.core.usecase.GetTotalIncomeOfCurrency
import com.lightfeather.core.usecase.GetTotalTransactionsByCategories
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import ui.entity.UiExpenseCategory
import ui.entity.toUiCategoryModel
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.home.model.toUiTransactionModel
import ui.pages.bottomnavigationpages.statistics.model.CurrencyData

class StatisticsViewModel(
    private val getAllTransactions: GetAllTransactions,
    private val getAllCurrencies: GetAllCurrencies,
    private val getTotalIncomeOfCurrency: GetTotalIncomeOfCurrency,
    private val getTotalExpenseOfCurrency: GetTotalExpenseOfCurrency,
    private val getAllExpensesOfCategories: GetTotalTransactionsByCategories<Transaction.Expense>,
    private val getAllIncomeOfCategories: GetTotalTransactionsByCategories<Transaction.Income>,
) : ViewModel() {
    private val jobDispatcher = SupervisorJob() + Dispatchers.IO

    private val _allTransactionsFlow = MutableStateFlow<List<UiTransactionModel>>(listOf())
    val allTransactionsFlow = _allTransactionsFlow.asStateFlow()

    private val _currenciesTotals = MutableStateFlow<List<CurrencyData>>(listOf())
    val currenciesTotals = _currenciesTotals.asStateFlow()

    private val _expensesTotalByCategory = MutableStateFlow<List<Pair<UiExpenseCategory, Double>>>(listOf())
    val expensesTotalByCategory = _expensesTotalByCategory.asStateFlow()

    private val _incomeTotalByCategory = MutableStateFlow<List<Pair<UiExpenseCategory, Double>>>(listOf())
    val incomeTotalByCategory = _incomeTotalByCategory.asStateFlow()

    fun loadData() {
        viewModelScope.launch(jobDispatcher) {
            getAllTransactions().map { it.map { it.toUiTransactionModel() } }.collect(_allTransactionsFlow)
        }

        viewModelScope.launch(jobDispatcher) {
            getAllCurrencies().map { currencies ->
                Napier.d(currencies.toString(), tag = "CURRENCIES")
                val res: List<CurrencyData> = currencies.map { currency ->
                    val totalIncome = getTotalIncomeOfCurrency(currency)
                    val totalExpense = getTotalExpenseOfCurrency(currency)
                    totalIncome.zip(totalExpense) { income: Double, expense: Double ->
                        CurrencyData(
                            id = currency.id.toString(),
                            currencyCode = currency.sign,
                            expenses = expense,
                            income = income
                        )
                    }.first()
                }
                return@map res
            }.collect(_currenciesTotals)
        }

        viewModelScope.launch(jobDispatcher) {
            getAllExpensesOfCategories().map {
                it.map { it.first.toUiCategoryModel() to it.second }.filter { it.second != 0.0 }
            }
                .collect(_expensesTotalByCategory)
        }

        viewModelScope.launch(jobDispatcher) {
            getAllIncomeOfCategories().map {
                it.map { it.first.toUiCategoryModel() to it.second }.filter { it.second != 0.0 }
            }
                .collect(_incomeTotalByCategory)
        }

    }

}