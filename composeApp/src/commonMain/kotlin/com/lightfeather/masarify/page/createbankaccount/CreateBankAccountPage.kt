package com.lightfeather.masarify.page.createbankaccount

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lightfeather.designsystem.model.UiBankAccount
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CreateBankAccountPage(
    account: UiBankAccount?,
    viewModel: CreateBankAccountPageViewModel = koinViewModel { parametersOf(account) }
) {
    val state by viewModel.state.collectAsState()
    CreateBankAccountPageContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
internal fun CreateBankAccountPageContent(
    state: CreateBankAccountPageState,
    onIntent: (CreateBankAccountPageIntent) -> Unit
) {
    // TODO: Implement form UI in separate task
    // Note: initialBalance text field will be disabled when state.inEditMode = true
    Text("Create/Edit Bank Account Form - To be implemented")
}
