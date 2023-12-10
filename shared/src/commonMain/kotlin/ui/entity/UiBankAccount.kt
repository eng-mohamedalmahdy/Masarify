package ui.entity

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.lightfeather.core.domain.Account

@Parcelize
data class UiBankAccount(
    val name: String,
    val description: String,
    val color: String,
    val logo: String,
    val balance: Double,
    val currency: UiCurrency,
    val id: Int = -1,
) : Parcelable

fun Account.toUiBankAccount() = UiBankAccount(
    name,
    description ?: "",
    color,
    logo,
    balance,
    currency.toUiCurrency(),
    id
)

fun UiBankAccount.toDomainBankAccount() = Account(
    id = id,
    name = name,
    currency = currency.toDomainCurrency(),
    description = description,
    balance = balance,
    color = color,
    logo = logo

)