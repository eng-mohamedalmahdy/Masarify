package ui.pages.userprimarydata

import ColorWheel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.lightfeather.masarify.MR
import data.local.setIsFirstTime
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.compose.stringResource
import ext.navigateSingleTop
import ext.toHexString
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ui.composeables.CardTextField
import ui.composeables.SaveButton
import ui.entity.UiBankAccount
import ui.entity.UiCurrency
import ui.main.LocalAppTheme
import ui.main.LocalMainNavController
import ui.main.LocalSettings
import ui.main.LocalSnackBarHostState
import ui.pages.Page
import ui.pages.createbankaccount.CreateBankAccountViewModel
import ui.util.SnackBarAction

@Composable
fun FillUserPrimaryDataPage() {
    val viewModel = CreateBankAccountViewModel(
        koinInject(),
        koinInject(),
        koinInject()
    ).let { viewModelFactory { it }.createViewModel() }

    val saveMessage = stringResource(MR.strings.saved_successfully)
    val hostState = LocalSnackBarHostState.current
    val coroutineScope = rememberCoroutineScope()

    val navController = LocalMainNavController.current
    val settings = LocalSettings.current
    val primaryColor = MaterialTheme.colorScheme.primary

    var accountName by remember { mutableStateOf("") }
    var accountDescription by remember { mutableStateOf("") }
    var bankBalance by remember { mutableStateOf("") }
    val bankColor = remember { mutableStateOf(primaryColor) }
    var isShowingColorPicker by remember { mutableStateOf(false) }

    var currencyName by remember { mutableStateOf("") }
    var currencySign by remember { mutableStateOf("") }

    val accountTitleMessage = stringResource(MR.strings.title_cant_be_empty)
    val balanceMessage = stringResource(MR.strings.balance_must_be_positive_number)
    val currencyNameMessage = stringResource(MR.strings.currency_name_cannot_be_empty)
    val currencySymbolMessage = stringResource(MR.strings.currency_symbol_is_required)

    fun getValidationErrorMessage(
        accountName: String,
        balance: String,
        currencyName: String,
        currencySymbol: String
    ): String? {
        return if (accountName.isEmpty()) accountTitleMessage
        else if ((balance.toDoubleOrNull() ?: 0.0) < -1) balanceMessage
        else if (currencyName.isEmpty()) currencyNameMessage
        else if (currencySymbol.isEmpty()) currencySymbolMessage
        else null
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
            label = { Text(stringResource(MR.strings.account_name)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CardTextField(
            text = accountDescription,
            onValueChange = { accountDescription = it },
            label = { Text(stringResource(MR.strings.description)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CardTextField(
            text = bankBalance,
            onValueChange = { bankBalance = it },
            label = { Text(stringResource(MR.strings.account_balance)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Decimal
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CardTextField(
                text = currencyName,
                onValueChange = { currencyName = it },
                label = { Text(stringResource(MR.strings.currency_name)) },
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(4.dp))

            CardTextField(
                text = currencySign,
                onValueChange = { currencySign = it },
                label = { Text(stringResource(MR.strings.currency_symbol)) },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        CardTextField(
            text = bankColor.value.toHexString(),
            onValueChange = {},
            label = { Text(stringResource(MR.strings.color), color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isShowingColorPicker = true },
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

        Spacer(modifier = Modifier.height(16.dp))

        SaveButton(onClick = {
            val errorMessage = getValidationErrorMessage(accountName, bankBalance, currencyName, currencySign)
            if (errorMessage == null) {
                viewModel.createBankAccountAndCurrency(
                    UiBankAccount(
                        name = accountName,
                        description = accountDescription,
                        balance = bankBalance.toDouble(),
                        currency = UiCurrency(0, currencyName, currencySign),
                        color = bankColor.value.toHexString(),
                        logo = "",
                        id = 0,
                    )
                )
                settings.setIsFirstTime(false)
                navController.navigateSingleTop { Page.AppHostPage }
            } else {
                coroutineScope.launch {
                    hostState.showSnackbar(
                        errorMessage,
                        SnackBarAction.Error(),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        })
    }

    if (isShowingColorPicker) {
        Dialog(onDismissRequest = { isShowingColorPicker = false }) {
            Card(
                Modifier.width(250.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                ColorWheel(
                    bankColor,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(LocalAppTheme.current.cardColor, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                )
                SaveButton { isShowingColorPicker = false }
                coroutineScope.launch {
                    hostState.showSnackbar(
                        saveMessage,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }
}
