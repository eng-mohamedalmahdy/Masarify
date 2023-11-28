package ui.pages.createtransactionpage.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.composeables.TransactionTabBar
import ui.pages.bottomnavigationpages.BottomNavigationPageModel
import ui.pages.bottomnavigationpages.home.model.UiTransactionType


@Composable
fun CreateTransactionPage(params: BottomNavigationPageModel.CreateTransactionPageModel) {
    CreateTransactionPageViews()
}

@Composable
private fun CreateTransactionPageViews() {
    var selectedTab by remember { mutableStateOf(UiTransactionType.Expense) }

    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(16.dp))
        TransactionTabBar(UiTransactionType.entries, selectedTab) { selectedTab = it }
        when (selectedTab) {
            UiTransactionType.TRANSFER -> CreateTransferPage()
            else -> CreateTransactionPage(selectedTab)
        }
    }


}

@Composable
private fun CreateTransactionPagePreview() {
// TODO: Implement call the views function here using dummy params

}