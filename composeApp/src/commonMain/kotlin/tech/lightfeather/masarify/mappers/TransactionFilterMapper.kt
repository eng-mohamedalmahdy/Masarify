package tech.lightfeather.masarify.mappers

import tech.lightfeather.designsystem.model.UiTransactionFilter
import tech.lightfeather.designsystem.model.UiTransactionType
import tech.lightfeather.domain.model.transaction.TransactionFilter

/**
 * Convert domain TransactionFilter to UI TransactionFilter
 */
fun TransactionFilter.toUiTransactionFilter(): UiTransactionFilter =
    UiTransactionFilter(
        ids = ids.map { it.toString() }.toSet(),
        accounts = accounts.map { it.toUiBankAccount() }.toSet(),
        categories = categories.map { it.toUiCategory() }.toSet(),
        currencies = currencies.map { it.toUiCurrency() }.toSet(),
        transactionTypes = transactionTypes.map { it.toUiTransactionType() }.toSet(),
        amountRange = amountRange?.toUiAmountRange(),
        dateRange = dateRange?.toUiDateRange(),
        textSearch = textSearch,
        hasAttachments = hasAttachments,
        logic = logic.toUiFilterLogic(),
    )

/**
 * Convert UI TransactionFilter to domain TransactionFilter
 */
fun UiTransactionFilter.toTransactionFilter(): TransactionFilter =
    TransactionFilter(
        ids = ids.mapNotNull { it.toIntOrNull() }.toSet(),
        accounts = accounts.map { it.toAccount() }.toSet(),
        categories = categories.map { it.toCategory() }.toSet(),
        currencies = currencies.map { it.toCurrency() }.toSet(),
        transactionTypes = transactionTypes.map { it.toTransactionType() }.toSet(),
        amountRange = amountRange?.toAmountRange(),
        dateRange = dateRange?.toDateRange(),
        textSearch = textSearch,
        hasAttachments = hasAttachments,
        logic = logic.toFilterLogic(),
    )

/**
 * Convert domain AmountRange to UI AmountRange
 */
private fun TransactionFilter.AmountRange.toUiAmountRange(): UiTransactionFilter.UiAmountRange =
    UiTransactionFilter.UiAmountRange(
        min = min?.toString(),
        max = max?.toString(),
    )

/**
 * Convert UI AmountRange to domain AmountRange
 */
private fun UiTransactionFilter.UiAmountRange.toAmountRange(): TransactionFilter.AmountRange =
    TransactionFilter.AmountRange(
        min = min?.toDoubleOrNull(),
        max = max?.toDoubleOrNull(),
    )

/**
 * Convert domain DateRange to UI DateRange
 */
private fun TransactionFilter.DateRange.toUiDateRange(): UiTransactionFilter.UiDateRange =
    UiTransactionFilter.UiDateRange(
        from = from,
        to = to,
    )

/**
 * Convert UI DateRange to domain DateRange
 */
private fun UiTransactionFilter.UiDateRange.toDateRange(): TransactionFilter.DateRange =
    TransactionFilter.DateRange(
        from = from,
        to = to,
    )

/**
 * Convert domain FilterLogic to UI FilterLogic
 */
private fun TransactionFilter.FilterLogic.toUiFilterLogic(): UiTransactionFilter.UiFilterLogic =
    when (this) {
        TransactionFilter.FilterLogic.AND -> UiTransactionFilter.UiFilterLogic.AND
        TransactionFilter.FilterLogic.OR -> UiTransactionFilter.UiFilterLogic.OR
    }

/**
 * Convert UI FilterLogic to domain FilterLogic
 */
private fun UiTransactionFilter.UiFilterLogic.toFilterLogic(): TransactionFilter.FilterLogic =
    when (this) {
        UiTransactionFilter.UiFilterLogic.AND -> TransactionFilter.FilterLogic.AND
        UiTransactionFilter.UiFilterLogic.OR -> TransactionFilter.FilterLogic.OR
    }

/**
 * Convert domain TransactionType to UI TransactionType
 */
private fun TransactionFilter.TransactionType.toUiTransactionType(): UiTransactionType =
    when (this) {
        TransactionFilter.TransactionType.INCOME -> UiTransactionType.INCOME
        TransactionFilter.TransactionType.EXPENSE -> UiTransactionType.EXPENSE
        TransactionFilter.TransactionType.TRANSFER -> UiTransactionType.TRANSFER
    }

/**
 * Convert UI TransactionType to domain TransactionType
 */
private fun UiTransactionType.toTransactionType(): TransactionFilter.TransactionType =
    when (this) {
        UiTransactionType.INCOME -> TransactionFilter.TransactionType.INCOME
        UiTransactionType.EXPENSE -> TransactionFilter.TransactionType.EXPENSE
        UiTransactionType.TRANSFER -> TransactionFilter.TransactionType.TRANSFER
    }
