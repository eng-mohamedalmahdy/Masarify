package com.lightfeather.designsystem.model

sealed interface TransactionListItem {
    data class TransactionEntry(
        val transaction: UiTransaction,
    ) : TransactionListItem

    data class StartOverMarker(
        val session: UiFinancialSession,
        val isExpanded: Boolean = false,
    ) : TransactionListItem
}
