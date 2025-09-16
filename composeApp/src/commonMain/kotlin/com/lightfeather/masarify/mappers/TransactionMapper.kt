package com.lightfeather.masarify.mappers

import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.domain.model.transaction.Transaction
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Transaction.toUiTransaction(): UiTransaction {
    val type = when (this) {
        is Transaction.Income -> UiTransactionType.INCOME
        is Transaction.Expense -> UiTransactionType.EXPENSE
        is Transaction.Transfer -> UiTransactionType.TRANSFER
    }

    val category = when (this) {
        is Transaction.Income -> source.toUiCategory()
        is Transaction.Expense -> categories.firstOrNull()?.toUiCategory() ?: com.lightfeather.designsystem.model.UiCategory.dummy
        is Transaction.Transfer -> com.lightfeather.designsystem.model.UiCategory.dummy
    }


    return UiTransaction(
        id = id.toString(),
        type = type,
        amount = amount.toString(),
        dateTime = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault()),
        description = description ?: name,
        category = category,
        hasAttachment = attachments.isNotEmpty(),
    )
}

@OptIn(ExperimentalTime::class)
fun UiTransaction.toTransactionIncome(
    account: com.lightfeather.domain.model.Account,
    source: com.lightfeather.domain.model.Category,
    attachments: List<com.lightfeather.domain.model.Attachment> = emptyList(),
): Transaction.Income =
    Transaction.Income(
        id = id.toIntOrNull() ?: -1,
        name = description,
        description = description,
        amount = parseAmount(amount),
        timestamp = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        account = account,
        source = source,
        attachments = attachments,
    )

@OptIn(ExperimentalTime::class)
fun UiTransaction.toTransactionExpense(
    account: com.lightfeather.domain.model.Account,
    categories: List<com.lightfeather.domain.model.Category>,
    attachments: List<com.lightfeather.domain.model.Attachment> = emptyList(),
): Transaction.Expense =
    Transaction.Expense(
        id = id.toIntOrNull() ?: -1,
        name = description,
        description = description,
        amount = parseAmount(amount),
        timestamp = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        account = account,
        categories = categories,
        attachments = attachments,
    )

@OptIn(ExperimentalTime::class)
fun UiTransaction.toTransactionTransfer(
    account: com.lightfeather.domain.model.Account,
    receiverAccount: com.lightfeather.domain.model.Account,
    fee: Double = 0.0,
    attachments: List<com.lightfeather.domain.model.Attachment> = emptyList(),
): Transaction.Transfer =
    Transaction.Transfer(
        id = id.toIntOrNull() ?: -1,
        name = description,
        description = description,
        amount = parseAmount(amount),
        timestamp = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        account = account,
        receiverAccount = receiverAccount,
        fee = fee,
        attachments = attachments,
    )

private fun parseAmount(amountString: String): Double =
    amountString
        .replace(Regex("[^\\d.-]"), "")
        .toDoubleOrNull() ?: 0.0
