package ui.entity

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.lightfeather.core.domain.Category

@Parcelize
data class UiExpenseCategory(
    val name: String,
    val icon: String,
    val color: String,
    val id: Int = 0,

    ) : Parcelable

fun Category.toUiCategoryModel() =
    UiExpenseCategory(name = name, icon = icon, color = color, id = id)

fun UiExpenseCategory.toDomainCategory() = Category(
    id, name, "", color, icon
)