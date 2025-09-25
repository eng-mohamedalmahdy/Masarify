package com.lightfeather.masarify.mappers

import com.lightfeather.designsystem.model.UiBankAccount
import com.lightfeather.domain.model.Account

fun Account.toUiBankAccount(): UiBankAccount =
    UiBankAccount(
        id = id.toString(),
        name = name,
        description = description,
        balance = formatBalance(balance),
        currency = currency.toUiCurrency(),
        color = color,
        image = logo.takeIf { it.isNotBlank() },
    )

fun UiBankAccount.toAccount(): Account =
    Account(
        id = id.toIntOrNull() ?: -1,
        name = name,
        description = description,
        balance = parseBalance(balance),
        currency = currency.toCurrency(),
        color = color,
        logo = (image as? String) ?: "",
    )

private fun formatBalance(
    balance: Double,
): String {
    return "$balance"
}

private fun parseBalance(balanceString: String): Double =
    balanceString
        .replace(Regex("[^\\d.-]"), "")
        .toDoubleOrNull() ?: 0.0
