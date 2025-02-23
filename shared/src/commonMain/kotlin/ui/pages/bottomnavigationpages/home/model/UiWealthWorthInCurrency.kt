package ui.pages.bottomnavigationpages.home.model

import com.lightfeather.core.domain.WealthWorthInCurrency
import ui.entity.UiCurrency
import ui.entity.toUiCurrency

data class UiWealthWorthInCurrency(
    val currency: UiCurrency,
    val worth: Double,
){
    companion object{
        val Dummy = UiWealthWorthInCurrency(UiCurrency.Dummy, 1000000.0)
    }
}

fun WealthWorthInCurrency.toUiWealthWorthInCurrency()= UiWealthWorthInCurrency(currency.toUiCurrency(), worth)
