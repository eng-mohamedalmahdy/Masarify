package ui.pages.createbankaccount

import ColorWheel
import LocalMainNavController
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.router.stack.pop
import com.lightfeather.masarify.MR
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.compose.stringResource
import ext.toHexString
import org.koin.compose.koinInject
import ui.composeables.CardTextField
import ui.composeables.DropDownTextField
import ui.composeables.SaveButton
import ui.entity.UiBankAccount
import ui.style.AppTheme

@Composable
fun CreateBankAccountPage() {
    val viewModel =
        CreateBankAccountViewModel(koinInject(), koinInject()).let { viewModelFactory { it }.createViewModel() }
    val navController = LocalMainNavController.current
    val viewModelState by remember { mutableStateOf(viewModel) }
    val currencies by viewModelState.currenciesFlow.collectAsState()
    val currenciesNames = currencies.map { it.name }
    val primaryColor = MaterialTheme.colorScheme.primary
    var accountName by remember { mutableStateOf("") }
    var accountDescription by remember { mutableStateOf("") }
    var bankBalance by remember { mutableStateOf("") }
    val bankColor = remember { mutableStateOf(primaryColor) }
    var selectedCurrencyIdx by remember { mutableStateOf(-1) }
    val selectedCurrency by remember { derivedStateOf { currencies.getOrNull(selectedCurrencyIdx) } }
    var isShowingColorPicker by remember { mutableStateOf(false) }
    if (isShowingColorPicker) {
        Dialog(onDismissRequest = { isShowingColorPicker = false }) {
            Card(Modifier.width(250.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                ColorWheel(
                    bankColor,
                    modifier = Modifier.size(200.dp)
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(AppTheme.cardColor, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                )
                SaveButton { isShowingColorPicker = false }
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(MR.strings.account_data))
        CardTextField(
            text = accountName,
            onValueChange = { accountName = it },
            label = { Text(stringResource(MR.strings.account_name)) },
            modifier = Modifier.fillMaxWidth(),
        )
        CardTextField(
            text = accountDescription,
            onValueChange = { accountDescription = it },
            label = { Text(stringResource(MR.strings.description)) },
            modifier = Modifier.fillMaxWidth(),
        )
        CardTextField(
            text = bankBalance,
            onValueChange = { bankBalance = it },
            label = { Text(stringResource(MR.strings.account_balance)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Decimal
        )

        DropDownTextField(currenciesNames, { Text(stringResource(MR.strings.currency)) }, { idx, item ->
            selectedCurrencyIdx = idx
        }, modifier = Modifier.fillMaxWidth())

        CardTextField(
            text = bankColor.value.toHexString(),
            onValueChange = {},
            label = { Text(stringResource(MR.strings.color), color = Color.Black) },
            modifier = Modifier.fillMaxWidth().clickable { isShowingColorPicker = true },
            readonlyAndDisabled = true,
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = bankColor.value,
                unfocusedContainerColor = bankColor.value,
                disabledContainerColor = bankColor.value,
            )
        )

        SaveButton(onClick = {
            selectedCurrency?.let {
                viewModel.createBankAccount(
                    UiBankAccount(
                        name = accountName,
                        description = accountDescription,
                        balance = bankBalance.toDouble(),
                        currency = it,
                        color = bankColor.value.toHexString(),
                    )
                )
                navController.pop {

                }
            }
        })
    }
}