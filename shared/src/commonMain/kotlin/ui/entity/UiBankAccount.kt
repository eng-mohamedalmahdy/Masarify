package ui.entity

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Currency
import ui.pages.bottomnavigationpages.home.model.UiExpenseModel

@Parcelize
data class UiBankAccount(
    val id: Int,
    val name: String,
    val description: String,
    val color: String,
    val balance: Double,
    val currency: UiCurrency,
) : Parcelable

fun Account.toUiBankAccount() = UiBankAccount(
    id,
    name,
    description ?: "",
    color,
    balance,
    currency.toUiCurrency()
)

fun UiBankAccount.toDomainBankAccount() = Account(
    id = id,
    name = name,
    currency = currency.toDomainCurrency(),
    description = description,
    balance = balance,
    color = color,
    logo = ""

)