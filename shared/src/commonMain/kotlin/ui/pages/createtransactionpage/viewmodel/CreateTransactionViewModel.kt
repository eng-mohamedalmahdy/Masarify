package ui.pages.createtransactionpage.viewmodel

import com.lightfeather.core.domain.DomainResult
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.core.usecase.CreateCategory
import com.lightfeather.core.usecase.CreateTransaction
import com.lightfeather.core.usecase.GetAllAccounts
import com.lightfeather.core.usecase.GetAllCategories
import com.lightfeather.core.usecase.GetAllCategoryIcons
import com.lightfeather.core.usecase.UpdateTransaction
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ui.entity.UiBankAccount
import ui.entity.UiExpenseCategory
import ui.entity.toDomainCategory
import ui.entity.toUiBankAccount
import ui.entity.toUiCategoryModel
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.home.model.toDomainTransaction

class CreateTransactionViewModel(
    private val createExpenseUseCase: CreateTransaction<Transaction.Expense>,
    private val createIncomeUseCase: CreateTransaction<Transaction.Income>,
    private val createTransferUseCase: CreateTransaction<Transaction.Transfer>,
    private val updateExpenseUseCase: UpdateTransaction<Transaction.Expense>,
    private val updateIncomeUseCase: UpdateTransaction<Transaction.Income>,
    private val updateTransferUseCase: UpdateTransaction<Transaction.Transfer>,
    private val getAllCategories: GetAllCategories,
    private val getAllBankAccounts: GetAllAccounts,
    private val createCategoryUseCase: CreateCategory,
    private val getAllCategoryIconsUseCase: GetAllCategoryIcons,

    ) : ViewModel() {


    private val _storedCategories = MutableStateFlow<List<UiExpenseCategory>>(listOf())
    val storedCategories = _storedCategories.asStateFlow()

    private val _storedBankAccounts = MutableStateFlow<List<UiBankAccount>>(listOf())
    val storedBankAccounts = _storedBankAccounts.asStateFlow()

    private val _categoryImages = MutableStateFlow<List<String>>(listOf())
    val categoryImages = _categoryImages.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getAllCategories().map { it.map { it.toUiCategoryModel() } }.collect(_storedCategories)
        }
        CoroutineScope(Dispatchers.IO).launch {
            getAllBankAccounts().map { it.map { it.toUiBankAccount() } }.collect(_storedBankAccounts)
        }

        CoroutineScope(Dispatchers.IO).launch {
            _categoryImages.emit(getAllCategoryIconsUseCase())
        }
    }

    fun createOrUpdateTransaction(oldExpenseModel: UiTransactionModel?, expenseModel: UiTransactionModel) {
        Napier.d(expenseModel.toDomainTransaction()::class.toString())
        if (oldExpenseModel == null) {
            CoroutineScope(Dispatchers.IO).launch {
                when (val transaction = expenseModel.toDomainTransaction()) {
                    is Transaction.Expense -> createExpenseUseCase(transaction)
                    is Transaction.Income -> createIncomeUseCase(transaction)
                    is Transaction.Transfer -> createTransferUseCase(transaction)
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                when (val transaction = expenseModel.toDomainTransaction()) {
                    is Transaction.Expense -> updateExpenseUseCase(oldExpenseModel.toDomainTransaction(), transaction)
                    is Transaction.Income -> updateIncomeUseCase(oldExpenseModel.toDomainTransaction(), transaction)
                    is Transaction.Transfer -> updateTransferUseCase(oldExpenseModel.toDomainTransaction(), transaction)
                }
            }
        }
    }

    fun createOrUpdateTransfer(
        oldExpenseModel: UiTransactionModel?,
        expenseModel: UiTransactionModel,
        onResult: (DomainResult<Int>) -> Unit
    ) {
        if (oldExpenseModel == null) {
            CoroutineScope(Dispatchers.IO).launch {
                onResult(
                    createTransferUseCase(expenseModel.toDomainTransaction() as Transaction.Transfer)
                )
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {

                onResult(
                    updateTransferUseCase(
                        oldExpenseModel.toDomainTransaction(),
                        expenseModel.toDomainTransaction() as Transaction.Transfer
                    ).let { DomainResult.Success(expenseModel.id) }
                )
            }
        }
    }

    fun createCategory(category: UiExpenseCategory) {
        CoroutineScope(Dispatchers.IO).launch {
            createCategoryUseCase(category.toDomainCategory())
        }
    }
}