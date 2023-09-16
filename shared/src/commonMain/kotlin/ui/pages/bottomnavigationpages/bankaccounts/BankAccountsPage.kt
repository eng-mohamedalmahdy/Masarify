package ui.pages.bottomnavigationpages.bankaccounts


import androidx.compose.runtime.Composable
import ui.pages.bottomnavigationpages.BottomNavigationPageModel


@Composable
fun BankAccountsPage(params: BottomNavigationPageModel.BankAccountsPageModel) {
    BankAccountsPageViews(params)
}

@Composable
private fun BankAccountsPageViews(params: BottomNavigationPageModel.BankAccountsPageModel) {
    // Your compose code here
}

@Composable
private fun BankAccountsPagePreview() {
// TODO: Implement call the views function here using dummy params
    BankAccountsPageViews(BottomNavigationPageModel.BankAccountsPageModel)
}