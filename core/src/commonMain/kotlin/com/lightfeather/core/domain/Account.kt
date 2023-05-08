package domain

typealias Accounts = List<Account>

data class Account(
    val id: Int,
    val name: String,
    val currency: Currency,
    val description: String?,
    val balance: Double,
    val color: String,
    val logo: String
)
