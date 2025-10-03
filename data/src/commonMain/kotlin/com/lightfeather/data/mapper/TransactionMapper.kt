package com.lightfeather.data.mapper

import com.lightfeather.data.local.database.model.DbTransactionType
import com.lightfeather.data.local.database.model.toDbTransactionType
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.Attachment
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.Currency
import com.lightfeather.domain.model.transaction.Transaction
import lightfeather.masarify.database.V_transactions

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
                            fileName = it.attachmentName.orEmpty(),
                            mimeType = it.attachmentMimeType.orEmpty(),
                            fileContent = it.attachmentData ?: byteArrayOf(),
                            transactionId = first.transactionId.toInt(),
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
                        )
                    }.distinctBy { it.id }

            when (first.transactionType?.toDbTransactionType()) {
                DbTransactionType.Income ->
                    Transaction.Income(
                        id = first.transactionId.toInt(),
                        name = first.transactionName.orEmpty(),
                        description = first.transactionDescription,
                        amount = first.transactionAmount ?: 0.0,
                        timestamp = first.transactionTimestamp ?: 0,
                        account = account,
                        source = categories.firstOrNull() ?: Category(-1, "Unknown", "", "#000000", "❓"),
                        attachments = attachments,
                    )

                DbTransactionType.Expense ->
                    Transaction.Expense(
                        id = first.transactionId.toInt(),
                        name = first.transactionName.orEmpty(),
                        description = first.transactionDescription,
                        amount = first.transactionAmount ?: 0.0,
                        timestamp = first.transactionTimestamp ?: 0,
                        account = account,
                        categories = categories,
                        attachments = attachments,
                    )

                DbTransactionType.Transfer ->
                    Transaction.Transfer(
                        id = first.transactionId.toInt(),
                        name = first.transactionName.orEmpty(),
                        description = first.transactionDescription,
                        amount = first.transactionAmount ?: 0.0,
                        timestamp = first.transactionTimestamp ?: 0,
                        account = account,
                        receiverAccount = receiverAccount,
                        fee = first.transactionFee ?: 0.0,
                        attachments = attachments,
                    )

                else -> throw IllegalArgumentException("Unknown transaction type: ${first.transactionType}")
            }
        }
