package com.lightfeather.designsystem.component.organisms.listitem

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransactionItem(transaction: UiTransaction) {

}


@Preview
@Composable
private fun PreviewTransactionItem() {
    AppTheme {
        TransactionItem(UiTransaction.dummy)
    }
}
