package com.lightfeather.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountSnapshot(
    val account: Account,
    val startingBalance: Double,
)

@Serializable
data class FinancialSession(
    val id: Int = -1,
    val timestamp: Long,
    val name: String? = null,
    val accountSnapshots: List<AccountSnapshot> = emptyList(),
)
