package ui.pages.bottomnavigationpages.bankaccounts.view


import ui.util.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import ext.OnUiStateChange
import ext.navigateSingleTop
import io.github.aakira.napier.Napier
import org.koin.compose.koinInject
import ui.entity.UiBankAccount
import ui.entity.UiCurrency
import ui.main.LocalMainNavController
import ui.pages.Page
import ui.pages.bottomnavigationpages.bankaccounts.viewmodel.BankAccountsViewModel


@Composable
fun BankAccountsPage() {
    val router = LocalMainNavController.current
    val viewModel: BankAccountsViewModel =
        BankAccountsViewModel(koinInject()).let { getViewModel(Unit, viewModelFactory { it }) }
    val bankAccounts by viewModel.bankAccountsFlow.collectAsState()
    OnUiStateChange(bankAccounts) {
        BankAccountsPageViews(it) { router.navigateSingleTop { Page.CreateBankAccountPageModel }; Napier.d("$it") }
    }
}

@Composable
private fun BankAccountsPageViews(bankAccounts: List<UiBankAccount>, onAddClick: () -> Unit) {
    if (bankAccounts.isEmpty()) {
        NoBankAccountsPlaceholder(onAddClick)
    } else {
        BankAccountsList(bankAccounts, onAccountClick = {}, onAddClick)
    }
}

@Preview
@Composable
private fun BankAccountsPagePreview() {
    BankAccountsPageViews(List(10) {
        UiBankAccount(
            name = "Janell Dunlap",
            description = "delicata",
            color = "finibus",
            balance = 4.5,
            currency = UiCurrency(id = 8965, name = "Jana McKenzie", sign = "persequeris"),
            id = 3330, logo = "scelerisque",
        )
    }) {}
}

@Preview
@Composable
private fun BankAccountsPagePreviewNoItems() {
    BankAccountsPageViews(listOf()) {}
}