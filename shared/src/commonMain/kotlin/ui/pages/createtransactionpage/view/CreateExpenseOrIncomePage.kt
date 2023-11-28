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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lightfeather.masarify.MR
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import ui.composeables.CategoriesPicker
import ui.composeables.DatePicker
import ui.composeables.DropDownTextField
import ui.composeables.CardTextField
import ui.composeables.SaveButton
import ui.entity.UiBankAccount
import ui.entity.UiExpenseCategory
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.home.model.UiTransactionType
import ui.pages.createtransactionpage.viewmodel.CreateTransactionViewModel


@Composable
fun CreateTransactionPage(transactionType: UiTransactionType) {

    val viewModel =
        CreateTransactionViewModel(
            koinInject(named("expense")), koinInject(named("income")), koinInject(named("transfer")), koinInject(), koinInject(), koinInject(),
            koinInject()
        ).let {
            getViewModel(Unit, viewModelFactory { it })
        }
    val viewModelState = remember { viewModel }
    val categories by viewModelState.storedCategories.collectAsState()
    val bankAccounts by viewModelState.storedBankAccounts.collectAsState()
    val categoryIcons by viewModelState.categoryImages.collectAsState()
    CreateExpensePageViews(
        transactionType,
        categories,
        bankAccounts,
        categoryIcons,
        onSaveClick = { viewModel.createTransaction(it) },
        onCreateCategory = { viewModel.createCategory(it) })
}

@Composable
private fun CreateExpensePageViews(
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

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    var time by remember { mutableStateOf("") }

    var category by remember { mutableStateOf<UiExpenseCategory?>(null) }
    var account by remember { mutableStateOf<UiBankAccount?>(null) }


    Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        if (isShowingDatePickerState) {

            Dialog(
                onDismissRequest = { isShowingDatePickerState = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
            ) {

                DatePicker(date, onDateSelected = {
                    date = it
                    isShowingDatePickerState = false
                }
                )
            }
        }

        if (isShowingTimePickerState) {

            TimePickerDialog(onConfirmClick = {
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
                    date.toString(),
                    onValueChange = {},
                    label = { Text(stringResource(MR.strings.date)) },
                    modifier = Modifier.clickable { isShowingDatePickerState = true }.weight(1f),
                    readonlyAndDisabled = true
                )
                Spacer(Modifier.width(8.dp))
                CardTextField(
                    time,
                    onValueChange = { },
                    label = { Text(stringResource(MR.strings.time)) },
                    modifier = Modifier.clickable { isShowingTimePickerState = true }.weight(1f),
                    readonlyAndDisabled = true
                )

            }
            DropDownTextField(
                bankAccounts.map { it.name },
                label = { Text(stringResource(MR.strings.bank_account)) },
                onValueChange = { idx: Int, _: String -> account = bankAccounts[idx] },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                CategoriesPicker(categories, onCategorySelect = { category = it },
                    onAddCategoryClick = {
                        isShowingAddCategory = true
                    })
            }
            Spacer(Modifier.heightIn(min = 16.dp).weight(1f))
            SaveButton(onClick = {
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
                                time,
                                date.toString()
                            )
                        )
                    }
                }
            })
            Spacer(Modifier.height(100.dp))

        }

    }
}

