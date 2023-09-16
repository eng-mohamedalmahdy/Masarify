package ui.pages.bottomnavigationpages.currencyconverter

import androidx.compose.runtime.Composable
import ui.pages.bottomnavigationpages.BottomNavigationPageModel


@Composable
fun CurrencyConverterPage(params: BottomNavigationPageModel.CurrencyConverterPageModel) {
    CurrencyConverterPageViews(params)
}

@Composable
private fun CurrencyConverterPageViews(params: BottomNavigationPageModel.CurrencyConverterPageModel) {

}

@Composable
private fun CurrencyConverterPagePreview() {
    CurrencyConverterPageViews(BottomNavigationPageModel.CurrencyConverterPageModel)
}