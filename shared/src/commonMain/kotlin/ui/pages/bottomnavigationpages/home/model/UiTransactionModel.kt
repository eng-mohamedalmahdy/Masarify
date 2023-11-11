package ui.pages.bottomnavigationpages.home.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.lightfeather.core.domain.Category
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
import ui.style.AppTheme.expenseColor
import ui.style.AppTheme.incomeColor
import ui.style.AppTheme.transferColor

@Parcelize
data class UiTransactionModel(
    val title: String,
    val description: String,
    val type: UiTransactionType,
    val amount: Double,
    val categories: List<UiExpenseCategory>,
    val currencySign: String,
    val account: UiBankAccount,
    val time: String = "",
    val date: String = "",
) : Parcelable

enum class UiTransactionType(val color: Color, val prefixChar: String) {
    Expense(expenseColor, "-"),
    INCOME(incomeColor, "+"),
    TRANSFER(transferColor, "►");

}

fun Transaction.getTypeFromDomainTransaction() = when (this) {
    is Transaction.Expense -> UiTransactionType.Expense
    is Transaction.Income -> UiTransactionType.INCOME
    is Transaction.Transfer -> UiTransactionType.TRANSFER
}

@Composable
fun UiTransactionType.getLocalisedString(): String = when (this) {
    UiTransactionType.Expense -> stringResource(MR.strings.expense)
    UiTransactionType.INCOME -> stringResource(MR.strings.income)
    UiTransactionType.TRANSFER -> stringResource(MR.strings.transfer)
}

fun Transaction.toUiExpenseModel(): UiTransactionModel {
    val categories: List<UiExpenseCategory> = when (this) {
        is Transaction.Expense -> categories
        is Transaction.Income -> listOf(source)
        is Transaction.Transfer -> listOf(Category.Transfer)
    }.map { it.toUiCategoryModel() }
    return UiTransactionModel(
        title = name,
        description = description ?: "",
        type = getTypeFromDomainTransaction(),
        amount = 0.0,
        categories = categories,
        currencySign = account.currency.sign,
        account = account.toUiBankAccount(),
        time = timestamp.formatTimeStampToTime(),
        date = timestamp.formatTimeStampToDate()
    )
}


fun UiTransactionModel.toDomainTransaction(
    receiverAccount: UiBankAccount? = null,
    transferFee: Double = 0.0
): Transaction =
    when (type) {
        UiTransactionType.Expense -> Transaction.Expense(
            name = title,
            id = 0,
            description = description,
            categories = categories.map { it.toDomainCategory() },
            amount = amount,
            timestamp = 0,
            account = account.toDomainBankAccount(),
        )

        UiTransactionType.INCOME -> Transaction.Income(
            name = title,
            id = 0,
            description = description,
            source = categories.map { it.toDomainCategory() }.first(),
            amount = amount,
            timestamp = 0,
            account = account.toDomainBankAccount(),
        )

        UiTransactionType.TRANSFER -> Transaction.Transfer(
            id = 0,
            name = title,
            description = description,
            amount = amount,
            timestamp = 0,
            account = account.toDomainBankAccount(),
            receiverAccount = receiverAccount!!.toDomainBankAccount(),
            fee = transferFee
        )
    }

