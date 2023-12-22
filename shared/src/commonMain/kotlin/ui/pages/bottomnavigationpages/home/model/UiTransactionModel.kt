package ui.pages.bottomnavigationpages.home.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.lightfeather.core.domain.Category
import com.lightfeather.core.domain.transaction.Transaction
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ext.convertTo24HourFormat
import ext.dateToTimestamp
import ext.formatTimeStampToDate
import ext.formatTimeStampToTime
import ui.entity.UiBankAccount
import ui.entity.UiExpenseCategory
import ui.entity.toDomainBankAccount
import ui.entity.toDomainCategory
import ui.entity.toUiBankAccount
import ui.entity.toUiCategoryModel
import ui.style.AppLightTheme.expenseColor
import ui.style.AppLightTheme.incomeColor
import ui.style.AppLightTheme.transferColor

@Parcelize
data class UiTransactionModel(
    val title: String,
    val description: String,
    val type: UiTransactionType,
    val amount: Double,
    val categories: List<UiExpenseCategory>,
    val currencySign: String,
    val account: UiBankAccount,
    val receiverAccount: UiBankAccount?,
    val fee: Double?,
    val time: String = "",
    val date: String = "",
    val id: Int,
) : Parcelable

enum class UiTransactionType(val color: Color, val prefixChar: String) {
    Expense(expenseColor, "-"),
    Income(incomeColor, "+"),
    Transfer(transferColor, "►");

}

fun Transaction.getTypeFromDomainTransaction() = when (this) {
    is Transaction.Expense -> UiTransactionType.Expense
    is Transaction.Income -> UiTransactionType.Income
    is Transaction.Transfer -> UiTransactionType.Transfer
}

@Composable
fun UiTransactionType.getLocalisedString(): String = when (this) {
    UiTransactionType.Expense -> stringResource(MR.strings.expense)
    UiTransactionType.Income -> stringResource(MR.strings.income)
    UiTransactionType.Transfer -> stringResource(MR.strings.transfer)
}

fun Transaction.toUiTransactionModel(): UiTransactionModel {
    val categories: List<UiExpenseCategory> = when (this) {
        is Transaction.Expense -> categories
        is Transaction.Income -> listOf(source)
        is Transaction.Transfer -> listOf(Category.Transfer)
    }.map { it.toUiCategoryModel() }
    val fee = if (this is Transaction.Transfer) fee else null
    val receiverAccount = if (this is Transaction.Transfer) receiverAccount else null
    return UiTransactionModel(
        title = name,
        description = description ?: "",
        type = getTypeFromDomainTransaction(),
        amount = amount,
        categories = categories,
        currencySign = account.currency.sign,
        account = account.toUiBankAccount(),
        time = timestamp.formatTimeStampToTime(),
        date = timestamp.formatTimeStampToDate(),
        fee = fee,
        receiverAccount = receiverAccount?.toUiBankAccount(),
        id = id
    )
}


fun UiTransactionModel.toDomainTransaction(): Transaction =
    when (type) {
        UiTransactionType.Expense -> Transaction.Expense(
            name = title,
            id = id,
            description = description,
            categories = categories.map { it.toDomainCategory() },
            amount = amount,
            timestamp = "${date}T${time.convertTo24HourFormat()}Z".dateToTimestamp(),
            account = account.toDomainBankAccount(),
        )

        UiTransactionType.Income -> Transaction.Income(
            name = title,
            id = id,
            description = description,
            source = categories.map { it.toDomainCategory() }.first(),
            amount = amount,
            timestamp = "${date}T${time.convertTo24HourFormat()}Z".dateToTimestamp(),
            account = account.toDomainBankAccount(),
        )

        UiTransactionType.Transfer -> Transaction.Transfer(
            id = id,
            name = title,
            description = description,
            amount = amount,
            timestamp = "${date}T${time.convertTo24HourFormat()}Z".dateToTimestamp(),
            account = account.toDomainBankAccount(),
            receiverAccount = receiverAccount!!.toDomainBankAccount(),
            fee = fee!!
        )
    }

