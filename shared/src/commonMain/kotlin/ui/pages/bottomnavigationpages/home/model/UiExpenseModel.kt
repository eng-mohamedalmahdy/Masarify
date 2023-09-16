package ui.pages.bottomnavigationpages.home.model

import androidx.compose.ui.graphics.Color
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.lightfeather.core.domain.transaction.Transaction
import ext.formatTimeStampToTime
import ui.entity.UiExpenseCategory
import ui.entity.toUiCategoryModel
import ui.style.expenseColor
import ui.style.incomeColor
import ui.style.transferColor

@Parcelize
data class UiExpenseModel(
    val title: String,
    val description: String,
    val type: UiExpenseType,
    val amount: Double,
    val time: String,
    val categories: List<UiExpenseCategory>,
    val currencySign: String
) : Parcelable

enum class UiExpenseType(val color: Color, val prefixChar: String) {
    EXPENSE(expenseColor, "-"),
    INCOME(incomeColor, "+"),
    TRANSFER(transferColor, "►");


}

fun Transaction.getTypeFromDomainTransaction() = when (this) {
    is Transaction.Expense -> UiExpenseType.EXPENSE
    is Transaction.Income -> UiExpenseType.INCOME
    is Transaction.Transfer -> UiExpenseType.TRANSFER
}

fun Transaction.toUiExpenseModel() = UiExpenseModel(
    title = name,
    description = description ?: "",
    type = getTypeFromDomainTransaction(),
    amount = 0.0,
    time = timestamp.formatTimeStampToTime(),
    categories = categories.map { it.toUiCategoryModel() },
    currencySign = account.currency.sign
)