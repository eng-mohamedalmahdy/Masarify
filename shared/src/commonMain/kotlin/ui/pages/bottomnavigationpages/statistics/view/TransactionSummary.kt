package ui.pages.bottomnavigationpages.statistics.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ui.main.LocalAppTheme
import ui.pages.bottomnavigationpages.statistics.model.CurrencyData

@Composable
fun TransactionSummaryByCurrency(transactionsData: List<CurrencyData>) {
    val appTheme = LocalAppTheme.current
    Column {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = appTheme.cardColor,
            )
        ) {
            CurrencyTable(
                transactionsData,
                Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
private fun CurrencyTable(data: List<CurrencyData>, modifier: Modifier = Modifier) {
    Box(modifier) {
        Table(
            headers = listOf(
                stringResource(MR.strings.currency),
                stringResource(MR.strings.expense),
                stringResource(MR.strings.income)
            )
        ) {
            data.forEach { currencyData ->
                TableRow(
                    cells = listOf(
                        currencyData.currencyCode,
                        currencyData.expenses.toString(),
                        currencyData.income.toString()
                    )
                )
            }
        }
    }
}

@Composable
private fun Table(headers: List<String>, content: @Composable () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray),
            horizontalArrangement = Arrangement.Center
        ) {
            headers.forEach { header ->
                TableItem(
                    text = header,
                    isHeader = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        content()
    }
}

@Composable
private fun TableRow(cells: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray),
        horizontalArrangement = Arrangement.Center
    ) {
        cells.forEach { cellValue ->
            TableItem(
                text = cellValue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TableItem(text: String, isHeader: Boolean = false, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(8.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            style = if (isHeader) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}