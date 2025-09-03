package com.lightfeather.domain.model

typealias Accounts = List<Account>

data class Account(
    val name: String,
    val currency: Currency,
    val description: String?,
    val balance: Double,
    val color: String,
    val logo: String,
    val id: Int = -1
)
