package ui.pages.createtransactionpage.viewmodel

import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.usecase.CreateTransaction
import com.lightfeather.core.usecase.GetAllAccounts
import com.lightfeather.core.usecase.GetAllCategories
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ui.entity.UiBankAccount
import ui.entity.UiExpenseCategory
import ui.entity.toUiBankAccount
import ui.entity.toUiCategoryModel
import ui.pages.bottomnavigationpages.home.model.UiExpenseModel
import ui.pages.bottomnavigationpages.home.model.toDomainTransaction

class CreateTransactionViewModel(
    private val getAllCategories: GetAllCategories,
    private val getAllBankAccounts: GetAllAccounts,
    private val createTransactionUseCase: CreateTransaction<Transaction.Expense>
) : ViewModel() {
    fun createTransaction(expenseModel: UiExpenseModel) {
        CoroutineScope(Dispatchers.IO).launch {
            createTransactionUseCase((expenseModel.toDomainTransaction()))

        }
    }

    private val _storedCategories = MutableStateFlow<List<UiExpenseCategory>>(listOf())
    val storedCategories = _storedCategories.asStateFlow()

    private val _storedBankAccounts = MutableStateFlow<List<UiBankAccount>>(listOf())
    val storedBankAccounts = _storedBankAccounts.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _storedCategories.emit(getAllCategories().map {
                it.toUiCategoryModel()
            })
        }
        CoroutineScope(Dispatchers.IO).launch {
            _storedBankAccounts.emit(
                getAllBankAccounts().map { it.toUiBankAccount() }
            )
        }
    }
}