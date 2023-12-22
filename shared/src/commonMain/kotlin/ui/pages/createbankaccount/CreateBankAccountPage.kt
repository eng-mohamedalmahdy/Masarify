package ui.pages.createbankaccount

import ColorWheel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ui.composeables.CardTextField
import ui.composeables.DropDownTextField
import ui.composeables.SaveButton
import ui.entity.UiBankAccount
import ui.entity.UiCurrency
import ui.main.LocalAppTheme
import ui.main.LocalMainNavController
import ui.main.LocalSnackBarHostState
import ui.util.SnackBarAction

@Composable
fun CreateBankAccountPage() {
    val viewModel =
        CreateBankAccountViewModel(
            koinInject(),
            koinInject(),
            koinInject()
        ).let { viewModelFactory { it }.createViewModel() }
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

    val snackbarHostState = LocalSnackBarHostState.current
    val coroutineScope = rememberCoroutineScope()

    val noCurrencySelected = stringResource(MR.strings.no_currency_selected)
    val bankAccountNameCantBeEmpty = stringResource(MR.strings.bank_name_cant_be_empty)
    val balanceMustPositive = stringResource(MR.strings.balance_must_be_positive_number)
    val successMessage = stringResource(MR.strings.saved_successfully)
    fun getValidationErrorMessage(
        selectedCurrency: UiCurrency?,
        accountName: String,
        bankBalance: String
    ): String? =
        if (selectedCurrency == null) noCurrencySelected
        else if (accountName.isEmpty()) bankAccountNameCantBeEmpty
        else if ((bankBalance.toDoubleOrNull() ?: -1.0) < 0.0) balanceMustPositive
        else null

    if (isShowingColorPicker) {
        Dialog(onDismissRequest = { isShowingColorPicker = false }) {
            Card(
                Modifier.width(250.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                ColorWheel(
                    bankColor,
                    modifier = Modifier.size(200.dp)
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(LocalAppTheme.current.cardColor, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                )
                SaveButton { isShowingColorPicker = false }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(MR.strings.account_data),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        CardTextField(
            text = accountName,
            onValueChange = { accountName = it },
            label = {
                Text(
                    stringResource(MR.strings.account_name),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
        CardTextField(
            text = accountDescription,
            onValueChange = { accountDescription = it },
            label = {
                Text(
                    stringResource(MR.strings.description),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
        CardTextField(
            text = bankBalance,
            onValueChange = { bankBalance = it },
            label = {
                Text(
                    stringResource(MR.strings.account_balance),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Decimal
        )

        DropDownTextField(
            "",
            currenciesNames,
            {
                Text(
                    stringResource(MR.strings.currency),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            { idx, item ->
                selectedCurrencyIdx = idx
            },
            modifier = Modifier.fillMaxWidth()
        )

        CardTextField(
            text = bankColor.value.toHexString(),
            onValueChange = {},
            label = {
                Text(
                    stringResource(MR.strings.color),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
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

        SaveButton {

            val validationErrorMessage = getValidationErrorMessage(selectedCurrency, accountName, bankBalance)

            if (validationErrorMessage == null) selectedCurrency?.let {
                viewModel.createBankAccount(
                    UiBankAccount(
                        name = accountName,
                        description = accountDescription,
                        balance = bankBalance.toDoubleOrNull() ?: -1.0,
                        currency = it,
                        color = bankColor.value.toHexString(), logo = "", id = 5657,
                    )
                )
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        successMessage,
                        SnackBarAction.Success(),
                        duration = SnackbarDuration.Short
                    )
                }
                navController.pop()
            }
            else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        validationErrorMessage,
                        SnackBarAction.Error(),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
}

