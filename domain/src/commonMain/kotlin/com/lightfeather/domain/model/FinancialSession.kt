package com.lightfeather.domain.model

data class AccountSnapshot(
    val account: Account,
    val startingBalance: Double,
)

data class FinancialSession(
    val id: Int = -1,
    val timestamp: Long,
    val name: String? = null,
    val accountSnapshots: List<AccountSnapshot> = emptyList(),
)
