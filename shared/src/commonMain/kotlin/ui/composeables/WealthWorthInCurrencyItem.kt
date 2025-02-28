package ui.composeables

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.lightfeather.core.domain.Currency
import ui.pages.bottomnavigationpages.home.model.UiWealthWorthInCurrency
import ui.util.Preview

@Composable
fun WealthWorthInCurrencyItem(
    wealthWorthInCurrency: UiWealthWorthInCurrency,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.height(34.dp),
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
                text = formatCurrencyTo2Decimal(wealthWorthInCurrency.worth),
                style = MaterialTheme.typography.titleLarge
            )
        }


    }
}

private fun formatCurrencyTo2Decimal(amount: Double): String {
    val decimalPart = amount.toInt()
    val fractionPart = (amount - decimalPart) * 100
    return "$decimalPart.${fractionPart.toInt()}"
}


@Preview
@Composable
private fun PreviewWealthWorthInCurrencyItem() {
    WealthWorthInCurrencyItem(
        wealthWorthInCurrency = UiWealthWorthInCurrency.Dummy
    )
}
