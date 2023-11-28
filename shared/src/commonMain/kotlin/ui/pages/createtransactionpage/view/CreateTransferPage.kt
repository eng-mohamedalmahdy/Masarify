package ui.pages.createtransactionpage.view

import androidx.compose.runtime.Composable
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lightfeather.masarify.MR
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import ui.composeables.DropDownTextField
import ui.composeables.CardTextField
import ui.composeables.SaveButton
import ui.entity.UiBankAccount
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.home.model.UiTransactionType
import ui.pages.createtransactionpage.viewmodel.CreateTransactionViewModel

@Composable
fun CreateTransferPage() {
    val viewModel =
        CreateTransactionViewModel(
            koinInject(named("expense")), koinInject(named("income")), koinInject(named("transfer")), koinInject(), koinInject(), koinInject(),
            koinInject()
        ).let {
            getViewModel(Unit, viewModelFactory { it })
        }
    val bankAccounts by viewModel.storedBankAccounts.collectAsState()
    CreateTransferPageViews(bankAccounts, viewModel::createTransfer)
}

@Composable
private fun CreateTransferPageViews(
    bankAccounts: List<UiBankAccount>,
    onSaveClick: (UiTransactionModel, receiverAccount: UiBankAccount, transferFee: Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("0.0") }
    var senderAccount by remember { mutableStateOf<UiBankAccount?>(null) }
    var receiverAccount by remember { mutableStateOf<UiBankAccount?>(null) }
    var fees by remember { mutableStateOf("0.0") }

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
        DropDownTextField(
            bankAccounts.map { it.name },
            label = { Text(stringResource(MR.strings.sender_account)) },
            onValueChange = { idx: Int, _: String -> senderAccount = bankAccounts[idx] },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )

        DropDownTextField(
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
            senderAccount?.let { sender ->
                receiverAccount?.let { receiver ->
                    if (sender != receiver) {
                        onSaveClick(
                            UiTransactionModel(
                                title = title,
                                description = description,
                                type = UiTransactionType.TRANSFER,
                                amount = amount.toDouble(),
                                categories = listOf(),
                                currencySign = sender.currency.sign,
                                account = sender
                            ),
                            receiver,
                            fees.toDoubleOrNull() ?: 0.0
                        )
                    }

                }
            }
        })
        Spacer(Modifier.height(100.dp))

    }


}

@Preview
@Composable
private fun CreateTransferPagePreview() {
    CreateTransferPageViews(listOf(),
        onSaveClick = { uiTransactionModel: UiTransactionModel, uiBankAccount: UiBankAccount, d: Double -> }
    )
}
