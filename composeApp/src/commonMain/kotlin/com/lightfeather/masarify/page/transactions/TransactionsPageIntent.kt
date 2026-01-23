package com.lightfeather.masarify.page.transactions

import com.lightfeather.designsystem.component.organisms.dialog.UiTransactionData
import com.lightfeather.designsystem.model.PageSize
import com.lightfeather.designsystem.model.SavedFilter
import com.lightfeather.designsystem.model.UiAttachment
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionFilter
import com.lightfeather.designsystem.model.UiTransactionType

/**
 * Intents for the Transactions Page
 * All user actions and events are represented as sealed intents
 */
sealed interface TransactionsPageIntent {
    // Data Loading
    data object LoadData : TransactionsPageIntent

    // Selection & Navigation
    data class SelectTransaction(
        val transaction: UiTransaction?,
    ) : TransactionsPageIntent

    // Filtering
    data class UpdateFilter(
        val filter: UiTransactionFilter,
    ) : TransactionsPageIntent

    data class SaveFilter(
        val name: String,
        val filter: UiTransactionFilter,
    ) : TransactionsPageIntent

    data class LoadSavedFilter(
        val filter: SavedFilter,
    ) : TransactionsPageIntent

    data class DeleteSavedFilter(
        val filter: SavedFilter,
    ) : TransactionsPageIntent

    data object ClearFilter : TransactionsPageIntent

    data object ShowFilterDialog : TransactionsPageIntent

    data object HideFilterDialog : TransactionsPageIntent

    // Pagination
    data class ChangePage(
        val page: Int,
    ) : TransactionsPageIntent

    data class ChangePageSize(
        val size: PageSize,
    ) : TransactionsPageIntent

    data object NextPage : TransactionsPageIntent

    data object PreviousPage : TransactionsPageIntent

    // CRUD Operations
    data object ShowAddDialog : TransactionsPageIntent

    data class ShowAddDialogWithType(
        val type: UiTransactionType,
        val fromAccountId: String? = null,
    ) : TransactionsPageIntent

    data class ShowEditDialog(
        val transaction: UiTransaction,
    ) : TransactionsPageIntent

    data object HideAddEditDialog : TransactionsPageIntent

    data class CreateTransaction(
        val data: UiTransactionData,
    ) : TransactionsPageIntent

    data class UpdateTransaction(
        val data: UiTransactionData,
    ) : TransactionsPageIntent

    data class DeleteTransaction(
        val transaction: UiTransaction,
    ) : TransactionsPageIntent

    data class DuplicateTransaction(
        val transaction: UiTransaction,
    ) : TransactionsPageIntent

    // Attachment Operations
    data object PickImages : TransactionsPageIntent

    data class DeleteAttachment(
        val attachment: UiAttachment,
    ) : TransactionsPageIntent

    data class LoadAttachments(
        val transactionId: String,
    ) : TransactionsPageIntent
}
