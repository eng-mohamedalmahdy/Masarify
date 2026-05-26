package tech.lightfeather.masarify.mappers

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import tech.lightfeather.designsystem.component.organisms.dialog.UiTransactionData
import tech.lightfeather.designsystem.model.UiCategory
import tech.lightfeather.designsystem.model.UiTransaction
import tech.lightfeather.designsystem.model.UiTransactionDetails
import tech.lightfeather.designsystem.model.UiTransactionType
import tech.lightfeather.domain.model.Attachment
import tech.lightfeather.domain.model.transaction.Transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun Transaction.toUiTransactionDetails(): UiTransactionDetails {
    val type =
        when (this) {
            is Transaction.Income -> UiTransactionType.INCOME
            is Transaction.Expense -> UiTransactionType.EXPENSE
            is Transaction.Transfer -> UiTransactionType.TRANSFER
        }

    val categories =
        when (this) {
            is Transaction.Income -> listOf(source.toUiCategory())
            is Transaction.Expense -> categories.map { it.toUiCategory() }
            is Transaction.Transfer -> emptyList()
        }

    return UiTransactionDetails(
        id = id.toString(),
        name = name,
        type = type,
        amount = amount.toString(),
        description = description ?: name,
        dateTime =
            Instant
                .fromEpochMilliseconds(timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
        categories = categories,
        attachments = attachments.map { it.toUiAttachment() },
        account = account.toUiBankAccount(),
        receiverAccount =
            if (this is Transaction.Transfer) {
                receiverAccount.toUiBankAccount()
            } else {
                null
            },
        transferFee =
            if (this is Transaction.Transfer) {
                fee.toString()
            } else {
                ""
            },
    )
}

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
            is Transaction.Expense -> categories.firstOrNull()?.toUiCategory() ?: UiCategory.empty
            is Transaction.Transfer -> UiCategory.empty
        }

    val currencySymbol = account.currency.sign
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
        balanceBefore = "$currencySymbol${formatBalanceAmount(accountOldBalance)}",
        balanceAfter = "$currencySymbol${formatBalanceAmount(accountNewBalance)}",
    )
}

fun UiTransactionDetails.toDomainTransaction(): Transaction {
    val account = account.toAccount()
    val attachments = attachments.map { it.toTransactionAttachment(transactionId = id.toInt()) }
    val amountValue = amount.toDouble()
    val timestampMillis =
        dateTime
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

    return when (type) {
        UiTransactionType.INCOME ->
            Transaction.Income(
                incomeId = id.toInt(),
                incomeName = name,
                incomeDescription = description.takeIf { it.isNotBlank() },
                incomeAmount = amountValue,
                incomeTimestamp = timestampMillis,
                incomeAccount = account,
                source = categories.firstOrNull()?.toCategory()!!,
                incomeAttachments = attachments,
            )

        UiTransactionType.EXPENSE ->
            Transaction.Expense(
                expenseId = id.toInt(),
                expenseName = name,
                expenseDescription = description.takeIf { it.isNotBlank() },
                expenseAmount = amountValue,
                expenseTimestamp = timestampMillis,
                expenseAccount = account,
                categories = categories.map { it.toCategory() },
                expenseAttachments = attachments,
            )

        UiTransactionType.TRANSFER ->
            Transaction.Transfer(
                transferId = id.toInt(),
                transferName = name,
                transferDescription = description.takeIf { it.isNotBlank() },
                transferAmount = amountValue,
                transferTimestamp = timestampMillis,
                transferAccount = account,
                receiverAccount = receiverAccount!!.toAccount(),
                fee = transferFee.toDoubleOrNull() ?: 0.0,
                transferAttachments = attachments,
            )
    }
}

@OptIn(ExperimentalTime::class)
fun UiTransaction.toTransactionIncome(attachments: List<Attachment> = emptyList()): Transaction.Income =
    Transaction.Income(
        incomeId = id.toIntOrNull() ?: -1,
        incomeName = description,
        incomeDescription = description,
        incomeAmount = parseAmount(amount),
        incomeTimestamp = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        incomeAccount = account.toAccount(),
        source = category.toCategory(),
        incomeAttachments = attachments,
    )

@OptIn(ExperimentalTime::class)
fun UiTransaction.toTransactionExpense(attachments: List<Attachment> = emptyList()): Transaction.Expense =
    Transaction.Expense(
        expenseId = id.toIntOrNull() ?: -1,
        expenseName = description,
        expenseDescription = description,
        expenseAmount = parseAmount(amount),
        expenseTimestamp = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        expenseAccount = account.toAccount(),
        categories = listOfNotNull(category.toCategory()),
        expenseAttachments = attachments,
    )

@OptIn(ExperimentalTime::class)
fun UiTransaction.toTransactionTransfer(attachments: List<Attachment> = emptyList()): Transaction.Transfer =
    Transaction.Transfer(
        transferId = id.toIntOrNull() ?: -1,
        transferName = description,
        transferDescription = description,
        transferAmount = parseAmount(amount),
        transferTimestamp = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        transferAccount = account.toAccount(),
        receiverAccount = receiverAccount!!.toAccount(),
        fee = transferFee?.toDoubleOrNull() ?: 0.0,
        transferAttachments = attachments,
    )

fun UiTransaction.toTransaction() =
    when (type) {
        UiTransactionType.EXPENSE -> toTransactionExpense()
        UiTransactionType.INCOME -> toTransactionIncome()
        UiTransactionType.TRANSFER -> toTransactionTransfer()
    }

private fun parseAmount(amountString: String): Double =
    amountString
        .replace(Regex("[^\\d.-]"), "")
        .toDoubleOrNull() ?: 0.0

private fun formatBalanceAmount(value: Double): String {
    val rounded = (value * 100).toLong() / 100.0
    val intPart = rounded.toLong()
    val decPart = ((rounded - intPart) * 100).toLong()
    val decStr = if (decPart < 0) (-decPart).toString().padStart(2, '0') else decPart.toString().padStart(2, '0')
    return "$intPart.$decStr"
}

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
    val transactionId = id?.toIntOrNull() ?: -1
    val domainAttachments = attachments.map { it.toTransactionAttachment(transactionId) }

    return when (type) {
        UiTransactionType.INCOME -> {
            val source = category?.toCategory()
            require(source != null) { "Income source required" }
            Transaction.Income(
                incomeId = transactionId,
                incomeName = name,
                incomeDescription = description,
                incomeAmount = parsedAmount,
                incomeTimestamp = timestamp,
                incomeAccount = domainAccount,
                source = source,
                incomeAttachments = domainAttachments,
            )
        }

        UiTransactionType.EXPENSE -> {
            val categories = listOfNotNull(category?.toCategory())
            require(categories.isNotEmpty()) { "Expense categories required" }
            Transaction.Expense(
                expenseId = transactionId,
                expenseName = name,
                expenseDescription = description,
                expenseAmount = parsedAmount,
                expenseTimestamp = timestamp,
                expenseAccount = domainAccount,
                categories = categories,
                expenseAttachments = domainAttachments,
            )
        }

        UiTransactionType.TRANSFER -> {
            val receiver = targetAccount?.toAccount()
            require(receiver != null) { "Transfer target account required" }
            val fee = transferFee?.let { parseAmount(it) } ?: 0.0
            Transaction.Transfer(
                transferId = transactionId,
                transferName = name,
                transferDescription = description,
                transferAmount = parsedAmount,
                transferTimestamp = timestamp,
                transferAccount = domainAccount,
                receiverAccount = receiver,
                fee = fee,
                transferAttachments = domainAttachments,
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
