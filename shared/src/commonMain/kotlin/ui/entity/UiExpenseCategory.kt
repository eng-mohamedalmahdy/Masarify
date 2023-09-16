package ui.entity

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.lightfeather.core.domain.Category

@Parcelize
data class UiExpenseCategory(
    val id: Int,
    val name: String,
    val icon: String,
    val color: String
) : Parcelable

fun Category.toUiCategoryModel() =
    UiExpenseCategory(id = id, name = name, icon = icon, color = color)