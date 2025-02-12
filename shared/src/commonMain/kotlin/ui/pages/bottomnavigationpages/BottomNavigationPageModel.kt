package ui.pages.bottomnavigationpages

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel


@Parcelize
sealed class BottomNavigationPageModel : Parcelable {

    data object HomePageModel : BottomNavigationPageModel()

    data object MorePageModel : BottomNavigationPageModel()

    data object StatisticsPageModel : BottomNavigationPageModel()

    data object CurrencyConverterPageModel : BottomNavigationPageModel()

    data object BankAccountsPageModel : BottomNavigationPageModel()

    data class CreateTransactionPageModel(val transaction:UiTransactionModel?) : BottomNavigationPageModel()

}