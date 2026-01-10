package com.lightfeather.masarify.mappers

import com.lightfeather.designsystem.component.organisms.dialog.UiTransactionData
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.model.UiTransaction
import com.lightfeather.designsystem.model.UiTransactionType
import com.lightfeather.domain.model.Attachment
import com.lightfeather.domain.model.transaction.Transaction
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Transaction.toUiTransaction(): UiTransaction {
    val type =
        when (this) {
            is Transaction.Income -> UiTransactionType.INCOME
            is Transaction.Expense -> UiTransactionType.EXPENSE
            is Transaction.Transfer -> UiTransactionType.TRANSFER
        }

    val category =
        when (this) {
            is Transaction.Income -> source.toUiCategory()
            is Transaction.Expense -> categories.first().toUiCategory()
            is Transaction.Transfer -> UiCategory.empty
        }

    return UiTransaction(
        id = id.toString(),
        type = type,
        amount = amount.toString(),
        dateTime = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault()),
        description = description ?: name,
        category = category,
        hasAttachment = attachments.isNotEmpty(),
        name = name,
        account = account.toUiBankAccount(),
        receiverAccount = if (this is Transaction.Transfer) receiverAccount.toUiBankAccount() else null,
        transferFee = if (this is Transaction.Transfer) fee.toString() else null,
    )
}

@OptIn(ExperimentalTime::class)
fun UiTransaction.toTransactionIncome(
    attachments: List<Attachment> = emptyList(),
): Transaction.Income =
    Transaction.Income(
        id = id.toIntOrNull() ?: -1,
        name = description,
        description = description,
        amount = parseAmount(amount),
        timestamp = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        account = account.toAccount(),
        source = category.toCategory(),
        attachments = attachments,
    )

@OptIn(ExperimentalTime::class)
fun UiTransaction.toTransactionExpense(
    attachments: List<Attachment> = emptyList(),
): Transaction.Expense =
    Transaction.Expense(
        id = id.toIntOrNull() ?: -1,
        name = description,
        description = description,
        amount = parseAmount(amount),
        timestamp = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        account = account.toAccount(),
        categories = listOfNotNull(category.toCategory()),
        attachments = attachments,
    )

@OptIn(ExperimentalTime::class)
fun UiTransaction.toTransactionTransfer(attachments: List<Attachment> = emptyList()): Transaction.Transfer =
    Transaction.Transfer(
        id = id.toIntOrNull() ?: -1,
        name = description,
        description = description,
        amount = parseAmount(amount),
        timestamp = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        account = account.toAccount(),
        receiverAccount = receiverAccount!!.toAccount(),
        fee = transferFee?.toDoubleOrNull() ?: 0.0,
        attachments = attachments,
    )

fun UiTransaction.toTransaction() = when (type) {
    UiTransactionType.EXPENSE -> toTransactionExpense()
    UiTransactionType.INCOME -> toTransactionIncome()
    UiTransactionType.TRANSFER -> toTransactionTransfer()
}

private fun parseAmount(amountString: String): Double =
    amountString
        .replace(Regex("[^\\d.-]"), "")
        .toDoubleOrNull() ?: 0.0

/**
 * Convert UiTransactionData to domain Transaction
 * Determines the appropriate Transaction subtype based on the type field
 */
@OptIn(ExperimentalTime::class)
@Suppress("ThrowsCount") // Validation requires multiple checks for different transaction types
fun UiTransactionData.toDomainTransaction(): Transaction {
    val timestamp = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    val parsedAmount = parseAmount(amount)
    val domainAccount = account.toAccount()

    return when (type) {
        UiTransactionType.INCOME -> {
            val source = category?.toCategory()
            require(source != null) { "Income source required" }
            Transaction.Income(
                id = id?.toIntOrNull() ?: -1,
                name = name,
                description = description,
                amount = parsedAmount,
                timestamp = timestamp,
                account = domainAccount,
                source = source,
                attachments = emptyList(),
            )
        }

        UiTransactionType.EXPENSE -> {
            val categories = listOfNotNull(category?.toCategory())
            require(categories.isNotEmpty()) { "Expense categories required" }
            Transaction.Expense(
                id = id?.toIntOrNull() ?: -1,
                name = name,
                description = description,
                amount = parsedAmount,
                timestamp = timestamp,
                account = domainAccount,
                categories = categories,
                attachments = emptyList(),
            )
        }

        UiTransactionType.TRANSFER -> {
            val receiver = targetAccount?.toAccount()
            require(receiver != null) { "Transfer target account required" }
            val fee = transferFee?.let { parseAmount(it) } ?: 0.0
            Transaction.Transfer(
                id = id?.toIntOrNull() ?: -1,
                name = name,
                description = description,
                amount = parsedAmount,
                timestamp = timestamp,
                account = domainAccount,
                receiverAccount = receiver,
                fee = fee,
                attachments = emptyList(),
            )
        }
    }
}

/**
 * Helper property to get current dateTime for new transactions
 */
@OptIn(ExperimentalTime::class)
private val dateTime:
    LocalDateTime
    get() =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
