package ui.composeables

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import ui.pages.bottomnavigationpages.home.model.UiWealthWorthInCurrency
import ui.util.Preview

@Composable
fun WealthWorthInCurrencyItem(
    wealthWorthInCurrency: UiWealthWorthInCurrency,
    modifier: Modifier = Modifier,
) {
    Card {
        Column(
            modifier = modifier.padding(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = wealthWorthInCurrency.currency.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = wealthWorthInCurrency.currency.sign,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = wealthWorthInCurrency.worth.toString(),
                style = MaterialTheme.typography.titleLarge
            )
        }


    }
}


@Preview
@Composable
private fun PreviewWealthWorthInCurrencyItem() {
    WealthWorthInCurrencyItem(
        wealthWorthInCurrency = UiWealthWorthInCurrency.Dummy
    )
}
