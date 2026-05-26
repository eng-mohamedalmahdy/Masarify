package tech.lightfeather.masarify.page.bankaccounts

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BankAccountsPageContentTest {
    @Test
    fun addAccountFabIsDisplayed() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    AccountsListPane(
                        accounts = emptyList(),
                        userAccountsCurrencies = emptyList(),
                        totalAmountInSelectedOrDefaultCurrency = "",
                        defaultCurrency = null,
                        selectedCurrency = null,
                        selectedAccount = null,
                        onAddAccount = {},
                        onAccountClick = {},
                        onUpdateAccount = {},
                        onDeleteAccount = {},
                        onCreateTransactionFromAccount = {},
                        onTransferFromAccount = {},
                        onCurrencyClick = {},
                    )
                }
            }

            onNodeWithTag("bank_accounts_add_fab").assertIsDisplayed()
        }
}
