package com.lightfeather.masarify.mappers

import com.lightfeather.designsystem.model.UiAccountSnapshot
import com.lightfeather.designsystem.model.UiFinancialSession
import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.AccountSnapshot
import com.lightfeather.domain.model.FinancialSession
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun FinancialSession.toUiFinancialSession(): UiFinancialSession =
    UiFinancialSession(
        id = id.toString(),
        timestamp =
            Instant
                .fromEpochMilliseconds(timestamp)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
        name = name,
        accountSnapshots = accountSnapshots.map { it.toUiAccountSnapshot() },
    )

fun AccountSnapshot.toUiAccountSnapshot(): UiAccountSnapshot =
    UiAccountSnapshot(
        accountId = account.id.toString(),
        accountName = account.name,
        accountColor = account.color,
        accountLogo = account.logo.orEmpty(),
        startingBalance = startingBalance.toString(),
        currencySymbol = account.currency.sign,
    )

fun UiAccountSnapshot.toAccountSnapshot(accountMap: Map<String, Account>): AccountSnapshot? {
    val account = accountMap[accountId] ?: return null
    return AccountSnapshot(
        account = account,
        startingBalance = startingBalance.toDoubleOrNull() ?: 0.0,
    )
}
