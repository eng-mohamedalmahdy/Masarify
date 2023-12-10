package ui.pages.createtransactionpage.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.arkivanov.decompose.router.stack.pop
import com.lightfeather.masarify.MR
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.compose.stringResource
import ext.formatTimeStampToTime
import ext.getAmPmFromTimeFormatted
import ext.getHoursFromTimeFormatted
import ext.getMinutesAndSecondsFromTimeFormatted
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.todayIn
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import ui.composeables.CardTextField
import ui.composeables.CategoriesPicker
import ui.composeables.DatePicker
import ui.composeables.DropDownTextField
import ui.composeables.SaveButton
import ui.entity.UiBankAccount
import ui.entity.UiExpenseCategory
import ui.main.LocalBottomNavigationNavController
import ui.main.LocalSnackBarHostState
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.home.model.UiTransactionType
import ui.pages.createtransactionpage.viewmodel.CreateTransactionViewModel
import ui.util.SnackBarAction


@Composable
fun CreateTransactionPage(transactionType: UiTransactionType, transactionModel: UiTransactionModel?) {

    val viewModel =
        CreateTransactionViewModel(
            koinInject(named("expense")),
            koinInject(named("income")),
            koinInject(named("transfer")),
            koinInject(named("expense")),
            koinInject(named("income")),
            koinInject(named("transfer")),
            koinInject(),
            koinInject(),
            koinInject(),
            koinInject()
        ).let {
            getViewModel(Unit, viewModelFactory { it })
        }
    val router = LocalBottomNavigationNavController.current
    val viewModelState = remember { viewModel }
    val categories by viewModelState.storedCategories.collectAsState()
    val bankAccounts by viewModelState.storedBankAccounts.collectAsState()
    val categoryIcons by viewModelState.categoryImages.collectAsState()
    CreateExpensePageViews(
        transactionModel,
        transactionType,
        categories,
        bankAccounts,
        categoryIcons,
        onSaveClick = { viewModel.createOrUpdateTransaction(transactionModel, it); router.pop() },
        onCreateCategory = { viewModel.createCategory(it) })
}

@Composable
private fun CreateExpensePageViews(
    initialTransactionModel: UiTransactionModel?,
    expenseType: UiTransactionType,
    categories: List<UiExpenseCategory>,
    bankAccounts: List<UiBankAccount>,
    categoryIcons: List<String>,
    onSaveClick: (UiTransactionModel) -> Unit,
    onCreateCategory: (UiExpenseCategory) -> Unit
) {
    var isShowingDatePickerState by remember { mutableStateOf(false) }
    var isShowingTimePickerState by remember { mutableStateOf(false) }
    var isShowingAddCategory by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf(initialTransactionModel?.title ?: "") }
    var description by remember { mutableStateOf(initialTransactionModel?.description ?: "") }
    var amount by remember { mutableStateOf("${initialTransactionModel?.amount ?: 0.0}") }
    var date by remember {
        mutableStateOf(
            initialTransactionModel?.date ?: Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        )
    }
    val timeNow = Clock.System.now().toEpochMilliseconds().formatTimeStampToTime()
    val timeNowHours = timeNow.getHoursFromTimeFormatted()
    val timeNowMinutes = timeNow.getMinutesAndSecondsFromTimeFormatted()
    val timeNowAmPm = timeNow.getAmPmFromTimeFormatted()
    var time by remember {
        mutableStateOf(
            Triple(
                initialTransactionModel?.time?.getHoursFromTimeFormatted() ?: timeNowHours,
                initialTransactionModel?.time?.getMinutesAndSecondsFromTimeFormatted() ?: timeNowMinutes,
                initialTransactionModel?.time?.getAmPmFromTimeFormatted() ?: timeNowAmPm
            )
        )
    }

    var category by remember { mutableStateOf<UiExpenseCategory?>(initialTransactionModel?.categories?.firstOrNull()) }
    var account by remember { mutableStateOf<UiBankAccount?>(initialTransactionModel?.account) }

    val accountError = stringResource(MR.strings.please_select_an_account)
    val categoryError = stringResource(MR.strings.please_select_a_category)
    val titleError = stringResource(MR.strings.title_cant_be_empty)
    val amountError = stringResource(MR.strings.amount_must_be_positive)
    val dateError = stringResource(MR.strings.please_select_date)
    val timeError = stringResource(MR.strings.please_select_time)
    val savedSuccessfully = stringResource(MR.strings.saved_successfully)
    val snackBarHostState = LocalSnackBarHostState.current
    val coroutineScope = rememberCoroutineScope()


    fun validateFields(
        account: UiBankAccount?,
        category: UiExpenseCategory?,
        title: String,
        amount: String,
        date: String,
        time: String
    ): String? =
        if (account == null) accountError
        else if (category == null) categoryError
        else if (title.isEmpty()) titleError
        else if ((amount.toDoubleOrNull() ?: -1.0) < 0.0) amountError
        else if (date.isEmpty()) dateError
        else if (time.isEmpty()) timeError
        else null


    Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        if (isShowingDatePickerState) {

            Dialog(
                onDismissRequest = { isShowingDatePickerState = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {

                DatePicker(date.toLocalDate(), onDateSelected = {
                    date = it.toString()
                    isShowingDatePickerState = false
                })
            }
        }

        if (isShowingTimePickerState) {

            TimePickerDialog(
                initialValue = time,
                onConfirmClick = {
                    time = it
                    isShowingTimePickerState = false
                }, onCancelClick = {
                    isShowingTimePickerState = false
                }, onDismissDialog = {
                    isShowingTimePickerState = false
                })
        }
        if (isShowingAddCategory) {
            CreateCategoryDialog(images = categoryIcons,
                onSaveCategoryClick = { onCreateCategory(it) },
                onDismissRequest = { isShowingAddCategory = false })
        }

        Column(Modifier.fillMaxSize()) {
            CardTextField(
                text = title,
                onValueChange = { title = it },
                label = { Text(stringResource(MR.strings.expense_title)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            CardTextField(
                text = description,
                onValueChange = { description = it },
                label = { Text(stringResource(MR.strings.description)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            CardTextField(
                text = amount,
                onValueChange = { amount = it },
                label = { Text(stringResource(MR.strings.amount)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                keyboardType = KeyboardType.Number
            )
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                CardTextField(
                    date,
                    onValueChange = {},
                    label = { Text(stringResource(MR.strings.date)) },
                    modifier = Modifier.clickable { isShowingDatePickerState = true }.weight(1f),
                    trailingIcon = Icons.Default.CalendarMonth,
                    readonlyAndDisabled = true
                )
                Spacer(Modifier.width(8.dp))
                CardTextField(
                    "${time.first}:${time.second} ${time.third}",
                    onValueChange = { },
                    label = { Text(stringResource(MR.strings.time)) },
                    modifier = Modifier.clickable { isShowingTimePickerState = true }.weight(1f),
                    trailingIcon = Icons.Default.Schedule,
                    readonlyAndDisabled = true
                )

            }
            DropDownTextField(
                initialValue = account?.name ?: "",
                bankAccounts.map { it.name },
                label = { Text(stringResource(MR.strings.bank_account)) },
                onValueChange = { idx: Int, _: String -> account = bankAccounts[idx] },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            Spacer(Modifier.heightIn(min = 4.dp))

            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                CategoriesPicker(
                    category,
                    categories,
                    onCategorySelect = { category = it }
                ) {
                    isShowingAddCategory = true
                }
            }
            Spacer(Modifier.heightIn(min = 16.dp).weight(1f))
            SaveButton(onClick = {
                val errorMessage = validateFields(account, category, title, amount, date, time.first)
                if (errorMessage == null) {
                    account?.let { account ->
                        category?.let { category ->
                            onSaveClick(
                                UiTransactionModel(
                                    title,
                                    description,
                                    expenseType,
                                    amount.toDoubleOrNull() ?: 0.0,
                                    listOf(category),
                                    account.currency.sign,
                                    account,
                                    null,
                                    null,
                                    "${time.first}:${time.second} ${time.third}",
                                    date,
                                    initialTransactionModel?.id ?: 0
                                )
                            )
                        }
                    }
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            savedSuccessfully,
                            SnackBarAction.Success(),
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            errorMessage,
                            SnackBarAction.Error(),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            })
            Spacer(Modifier.height(100.dp))

        }

    }
}

