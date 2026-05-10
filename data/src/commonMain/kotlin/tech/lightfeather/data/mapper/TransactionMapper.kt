package tech.lightfeather.data.mapper

import lightfeather.masarify.database.V_transactions
import tech.lightfeather.data.local.database.model.DbTransactionType
import tech.lightfeather.data.local.database.model.toDbTransactionType
import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.Attachment
import tech.lightfeather.domain.model.Category
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.transaction.Transaction

// Complex mapper with multiple transaction type transformations
@Suppress("CyclomaticComplexMethod")
fun List<V_transactions>.toDomainTransactions(): List<Transaction> =
    this
        .groupBy { it.transactionId }
        .map { (_, rows) ->
            val first = rows.first()

            val account =
                Account(
                    id = first.accountId.toInt(),
                    name = first.accountName,
                    description = first.accountDescription,
                    balance = first.accountBalance,
                    color = first.accountColor,
                    logo = first.accountLogo.orEmpty(),
                    currency =
                        Currency(
                            name = first.currencyName,
                            sign = first.currencySign,
                            id = first.currencyId.toInt(),
                        ),
                )

            val receiverAccount =
                Account(
                    id = (first.receiverAccountId ?: -1).toInt(),
                    name = first.receiverAccountName.orEmpty(),
                    description = first.receiverAccountDescription.orEmpty(),
                    balance = first.receiverAccountBalance ?: 0.0,
                    color = first.receiverAccountColor.orEmpty(),
                    logo = first.receiverAccountLogo.orEmpty(),
                    currency =
                        Currency(
                            name = first.receiverCurrencyName.orEmpty(),
                            sign = first.receiverCurrencySign.orEmpty(),
                            id = first.receiverCurrencyId?.toInt() ?: -1,
                        ),
                )

            val attachments =
                rows
                    .filter { it.attachmentId != null }
                    .map {
                        Attachment(
                            id = it.attachmentId!!.toInt(),
                            entityType = tech.lightfeather.domain.model.AttachmentEntityType.TRANSACTION,
                            entityId = first.transactionId.toInt(),
                            fileName = it.attachmentName.orEmpty(),
                            mimeType = it.attachmentMimeType.orEmpty(),
                            fileContent = it.attachmentData ?: byteArrayOf(),
                        )
                    }.distinctBy { it.id }

            val categories =
                rows
                    .filter { it.categoryId != null }
                    .map {
                        Category(
                            id = it.categoryId?.toInt()!!,
                            name = it.categoryName!!,
                            description = it.categoryDescription!!,
                            color = it.categoryColor!!,
                            icon = it.categoryIcon!!,
                            isDefault = it.categoryIsDefault == 1L,
                            resourceKey = it.categoryResourceKey,
                        )
                    }.distinctBy { it.id }

            when (first.transactionType?.toDbTransactionType()) {
                DbTransactionType.Income ->
                    Transaction.Income(
                        incomeId = first.transactionId.toInt(),
                        incomeName = first.transactionName.orEmpty(),
                        incomeDescription = first.transactionDescription,
                        incomeAmount = first.transactionAmount ?: 0.0,
                        incomeTimestamp = first.transactionTimestamp ?: 0,
                        incomeAccount = account,
                        source = categories.firstOrNull() ?: Category(-1, "Unknown", "", "#000000", "❓"),
                        incomeAttachments = attachments,
                    )

                DbTransactionType.Expense ->
                    Transaction.Expense(
                        expenseId = first.transactionId.toInt(),
                        expenseName = first.transactionName.orEmpty(),
                        expenseDescription = first.transactionDescription,
                        expenseAmount = first.transactionAmount ?: 0.0,
                        expenseTimestamp = first.transactionTimestamp ?: 0,
                        expenseAccount = account,
                        categories = categories,
                        expenseAttachments = attachments,
                    )

                DbTransactionType.Transfer ->
                    Transaction.Transfer(
                        transferId = first.transactionId.toInt(),
                        transferName = first.transactionName.orEmpty(),
                        transferDescription = first.transactionDescription,
                        transferAmount = first.transactionAmount ?: 0.0,
                        transferTimestamp = first.transactionTimestamp ?: 0,
                        transferAccount = account,
                        receiverAccount = receiverAccount,
                        fee = first.transactionFee ?: 0.0,
                        transferAttachments = attachments,
                    )

                else -> throw IllegalArgumentException("Unknown transaction type: ${first.transactionType}")
            }
        }
