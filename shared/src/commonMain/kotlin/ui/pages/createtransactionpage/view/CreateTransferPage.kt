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
import com.lightfeather.core.domain.DomainResult
import com.lightfeather.masarify.MR
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.compose.stringResource
import ext.formatTimeStampToTime
import ext.getAmPmFromTimeFormatted
import ext.getHoursFromTimeFormatted
import ext.getMinutesAndSecondsFromTimeFormatted
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import ui.composeables.CardTextField
import ui.composeables.DatePicker
import ui.composeables.DropDownTextField
import ui.composeables.SaveButton
import ui.entity.UiBankAccount
import ui.main.LocalBottomNavigationNavController
import ui.main.LocalSnackBarHostState
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.home.model.UiTransactionType
import ui.pages.createtransactionpage.viewmodel.CreateTransactionViewModel
import ui.util.SnackBarAction

@Composable
fun CreateTransferPage(transactionModel: UiTransactionModel?) {
    val savedSuccessfully = stringResource(MR.strings.saved_successfully)

    val bottomNavRouter = LocalBottomNavigationNavController.current

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
    val bankAccounts by viewModel.storedBankAccounts.collectAsState()
    val snackBarHostState = LocalSnackBarHostState.current
    CreateTransferPageViews(transactionModel, bankAccounts) { old, new ->
        viewModel.createOrUpdateTransfer(old, new) {
            MainScope().launch {
                if (it is DomainResult.Success) {
                    bottomNavRouter.pop()
                    snackBarHostState.showSnackbar(
                        savedSuccessfully,
                        SnackBarAction.Success(),
                        duration = SnackbarDuration.Short
                    )
                } else if (it is DomainResult.Failure) {
                    snackBarHostState.showSnackbar(
                        it.throwable.toString(),
                        SnackBarAction.Success(),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateTransferPageViews(
    initialTransactionModel: UiTransactionModel?,
    bankAccounts: List<UiBankAccount>,
    onSaveClick: (UiTransactionModel?, UiTransactionModel) -> Unit
) {

    var isShowingDatePickerState by remember { mutableStateOf(false) }
    var isShowingTimePickerState by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf(initialTransactionModel?.title ?: "") }
    var description by remember { mutableStateOf(initialTransactionModel?.description ?: "") }
    var amount by remember { mutableStateOf("${initialTransactionModel?.amount ?: 0.0}") }
    var date by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }

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
    var senderAccount by remember { mutableStateOf<UiBankAccount?>(initialTransactionModel?.account) }
    var receiverAccount by remember { mutableStateOf<UiBankAccount?>(initialTransactionModel?.receiverAccount) }
    var fees by remember { mutableStateOf("${initialTransactionModel?.fee ?: 0.0}") }


    val accountError = stringResource(MR.strings.please_select_an_account)
    val receiverAccountError = stringResource(MR.strings.please_select_receiver_account)
    val currencyError = stringResource(MR.strings.transaction_accounts_must_have_the_same_currency)
    val titleError = stringResource(MR.strings.title_cant_be_empty)
    val amountError = stringResource(MR.strings.amount_must_be_positive)
    val dateError = stringResource(MR.strings.please_select_date)
    val timeError = stringResource(MR.strings.please_select_time)
    val snackBarHostState = LocalSnackBarHostState.current
    val coroutineScope = rememberCoroutineScope()


    fun validateFields(
        account: UiBankAccount?,
        receiverAccount: UiBankAccount?,
        title: String,
        amount: String,
        date: String,
        time: String
    ): String? =
        if (account == null) accountError
        else if (receiverAccount == null) receiverAccountError
        else if (title.isEmpty()) titleError
        else if ((amount.toDoubleOrNull() ?: -1.0) < 0.0) amountError
        else if (date.isEmpty()) dateError
        else if (time.isEmpty()) timeError
        else null



    Box {
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

            TimePickerDialog(initialValue = time, onConfirmClick = {
                time = it
                isShowingTimePickerState = false
            }, onCancelClick = {
                isShowingTimePickerState = false
            }, onDismissDialog = {
                isShowingTimePickerState = false
            })
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Text(
                stringResource(MR.strings.record_money_transfer),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

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
                amount,
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
                senderAccount?.name ?: "",
                bankAccounts.map { it.name },
                label = { Text(stringResource(MR.strings.sender_account)) },
                onValueChange = { idx: Int, _: String -> senderAccount = bankAccounts[idx] },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )

            DropDownTextField(
                receiverAccount?.name ?: "",
                bankAccounts.map { it.name },
                label = { Text(stringResource(MR.strings.receiver_account)) },
                onValueChange = { idx: Int, _: String -> receiverAccount = bankAccounts[idx] },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            CardTextField(
                fees,
                onValueChange = { fees = it },
                label = { Text(stringResource(MR.strings.transfer_fees)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                keyboardType = KeyboardType.Number
            )

            Spacer(Modifier.height(16.dp))
            SaveButton(onClick = {
                val errorMessage = validateFields(
                    senderAccount,
                    receiverAccount,
                    title,
                    amount,
                    date.toString(),
                    time.toString()
                )
                if (errorMessage == null) {
                    senderAccount?.let { sender ->
                        receiverAccount?.let { receiver ->
                            if (sender != receiver) {
                                onSaveClick(
                                    initialTransactionModel,
                                    UiTransactionModel(
                                        title = title,
                                        description = description,
                                        type = UiTransactionType.TRANSFER,
                                        amount = amount.toDouble(),
                                        date = date.toString(),
                                        time = "${time.first}:${time.second} ${time.third}",
                                        categories = listOf(),
                                        currencySign = sender.currency.sign,
                                        account = sender,
                                        receiverAccount = receiver,
                                        fee = fees.toDoubleOrNull(),
                                        id = initialTransactionModel?.id ?: 0
                                    ),
                                )
                            }

                        }
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