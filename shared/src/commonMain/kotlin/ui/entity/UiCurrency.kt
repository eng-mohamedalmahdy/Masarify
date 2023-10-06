package ui.entity

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.lightfeather.core.domain.Currency

@Parcelize
data class UiCurrency(
    val id: Int,
    val name: String,
    val sign: String,
) : Parcelable

fun Currency.toUiCurrency() = UiCurrency(id, name, sign)

fun UiCurrency.toDomainCurrency() = Currency(id, name, sign)
