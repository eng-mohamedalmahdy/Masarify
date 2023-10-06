package ui.pages.bottomnavigationpages.home.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ext.formatTimeStampToDate
import ext.formatTimeStampToTime
import ui.entity.UiBankAccount
import ui.entity.UiExpenseCategory
import ui.entity.toDomainBankAccount
import ui.entity.toDomainCategory
import ui.entity.toUiBankAccount
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
    val date: String,
    val time: String,
    val categories: List<UiExpenseCategory>,
    val currencySign: String,
    val account: UiBankAccount,
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

@Composable
fun UiExpenseType.getLocalisedString(): String = when (this) {
    UiExpenseType.EXPENSE -> stringResource(MR.strings.expense)
    UiExpenseType.INCOME -> stringResource(MR.strings.income)
    UiExpenseType.TRANSFER -> stringResource(MR.strings.transfer)
}

fun Transaction.toUiExpenseModel() = UiExpenseModel(
    title = name,
    description = description ?: "",
    type = getTypeFromDomainTransaction(),
    amount = 0.0,
    date = timestamp.formatTimeStampToDate(),
    time = timestamp.formatTimeStampToTime(),
    categories = categories.map { it.toUiCategoryModel() },
    currencySign = account.currency.sign,
    account = account.toUiBankAccount()
)

fun UiExpenseModel.toDomainTransaction(): Transaction.Expense =
    Transaction.Expense(
        name = title,
        id = 0,
        description = description,
        category = categories.map { it.toDomainCategory() },
        amount = amount,
        timestamp = 0,
        account = account.toDomainBankAccount(),
    )

