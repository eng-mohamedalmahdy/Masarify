package ui.pages.createtransactionpage.viewmodel

import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.usecase.CreateCategory
import com.lightfeather.core.usecase.CreateTransaction
import com.lightfeather.core.usecase.GetAllAccounts
import com.lightfeather.core.usecase.GetAllCategories
import com.lightfeather.core.usecase.GetAllCategoryIcons
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ui.entity.UiBankAccount
import ui.entity.UiExpenseCategory
import ui.entity.toDomainCategory
import ui.entity.toUiBankAccount
import ui.entity.toUiCategoryModel
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.home.model.toDomainTransaction

class CreateTransactionViewModel(
    private val getAllCategories: GetAllCategories,
    private val getAllBankAccounts: GetAllAccounts,
    private val createTransactionUseCase: CreateTransaction<Transaction>,
    private val createCategoryUseCase: CreateCategory,
    private val getAllCategoryIconsUseCase: GetAllCategoryIcons
) : ViewModel() {


    private val _storedCategories = MutableStateFlow<List<UiExpenseCategory>>(listOf())
    val storedCategories = _storedCategories.asStateFlow()

    private val _storedBankAccounts = MutableStateFlow<List<UiBankAccount>>(listOf())
    val storedBankAccounts = _storedBankAccounts.asStateFlow()

    private val _categoryImages = MutableStateFlow<List<String>>(listOf())
    val categoryImages = _categoryImages.asStateFlow()

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

        CoroutineScope(Dispatchers.IO).launch {
            _categoryImages.emit(getAllCategoryIconsUseCase())
        }
    }

    fun createTransaction(expenseModel: UiTransactionModel) {
        CoroutineScope(Dispatchers.IO).launch {
            createTransactionUseCase((expenseModel.toDomainTransaction()))
        }
    }

    fun createTransfer(expenseModel: UiTransactionModel, receiverAccount: UiBankAccount, transferFee: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            createTransactionUseCase((expenseModel.toDomainTransaction(receiverAccount, transferFee)))
        }
    }

    fun createCategory(category: UiExpenseCategory) {
        CoroutineScope(Dispatchers.IO).launch {
            createCategoryUseCase(category.toDomainCategory())
        }
    }
}