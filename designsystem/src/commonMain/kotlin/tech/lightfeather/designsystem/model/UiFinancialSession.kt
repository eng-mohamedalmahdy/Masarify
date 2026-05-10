package tech.lightfeather.designsystem.model

import kotlinx.datetime.LocalDateTime

data class UiAccountSnapshot(
    val accountId: String,
    val accountName: String,
    val accountColor: String,
    val accountLogo: String,
    val startingBalance: String,
    val currencySymbol: String,
)

data class UiFinancialSession(
    val id: String,
    val timestamp: LocalDateTime,
    val name: String?,
    val accountSnapshots: List<UiAccountSnapshot>,
)
