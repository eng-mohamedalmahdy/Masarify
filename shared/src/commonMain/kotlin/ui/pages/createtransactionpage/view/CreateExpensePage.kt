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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
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
import ui.composeables.CategoriesPicker
import ui.composeables.DatePicker
import ui.composeables.DropDownTextField
import ui.composeables.LineOnlyTextField
import ui.entity.UiBankAccount
import ui.entity.UiExpenseCategory
import ui.pages.bottomnavigationpages.home.model.UiExpenseModel
import ui.pages.bottomnavigationpages.home.model.UiExpenseType
import ui.pages.createtransactionpage.viewmodel.CreateTransactionViewModel


@Composable
fun CreateExpensePage() {

    val viewModel =
        CreateTransactionViewModel(koinInject(), koinInject(), koinInject()).let {
            getViewModel(Unit, viewModelFactory { it })
        }
    val viewModelState = remember { viewModel }
    val categories by viewModelState.storedCategories.collectAsState()
    val bankAccounts by viewModelState.storedBankAccounts.collectAsState()
    CreateExpensePageViews(categories, bankAccounts, onSaveClick = { viewModel.createTransaction(it) })
}

@Composable
private fun CreateExpensePageViews(
    categories: List<UiExpenseCategory>,
    bankAccounts: List<UiBankAccount>,
    onSaveClick: (UiExpenseModel) -> Unit
) {
    var isShowingDatePickerState by remember { mutableStateOf(false) }
    var isShowingTimePickerState by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    var time by remember { mutableStateOf("") }

    var category by remember { mutableStateOf<UiExpenseCategory?>(null) }
    var account by remember { mutableStateOf<UiBankAccount?>(null) }



    Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        Column(Modifier.fillMaxSize()) {
            LineOnlyTextField(
                text = title,
                onValueChange = { title = it },
                label = { Text(stringResource(MR.strings.expense_title)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            LineOnlyTextField(
                text = description,
                onValueChange = { description = it },
                label = { Text(stringResource(MR.strings.description)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            LineOnlyTextField(
                text = amount,
                onValueChange = { amount = it },
                label = { Text(stringResource(MR.strings.amount)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                keyboardType = KeyboardType.Number
            )
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                LineOnlyTextField(
                    date.toString(),
                    onValueChange = {},
                    label = { Text(stringResource(MR.strings.date)) },
                    modifier = Modifier.clickable { isShowingDatePickerState = true }.weight(1f),
                    readonlyAndDisabled = true
                )
                Spacer(Modifier.width(8.dp))
                LineOnlyTextField(
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
            Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                CategoriesPicker(categories, onCategorySelect = { category = it })
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    if (account != null && category != null)
                        account?.let { account ->
                            category?.let { category ->
                                onSaveClick(
                                    UiExpenseModel(
                                        title,
                                        description,
                                        UiExpenseType.EXPENSE,
                                        amount.toDoubleOrNull() ?: 0.0,
                                        date.toString(),
                                        time,
                                        listOf(category),
                                        account.currency.sign,
                                        account
                                    )
                                )
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(stringResource(MR.strings.save))
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.Save, stringResource(MR.strings.save))
            }
            Spacer(Modifier.height(100.dp))

        }
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

    }
}

