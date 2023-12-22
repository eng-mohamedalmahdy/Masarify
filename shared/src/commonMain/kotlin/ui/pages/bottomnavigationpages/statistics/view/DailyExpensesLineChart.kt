package ui.pages.bottomnavigationpages.statistics.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ui.main.LocalAppTheme
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.home.model.UiTransactionType

@Composable
fun DailyExpensesLineChart(
    datesExpenses: List<UiTransactionModel>,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current
    val xs = datesExpenses.groupBy { it.date }.keys.toList()

    val expensesWithDate = datesExpenses.groupBy { it.date }
        .mapValues { (_, transactionsForDate) ->
            transactionsForDate
                .filter { it.type == UiTransactionType.Expense }
                .sumOf { it.amount }
        }

    val incomeWithDate = datesExpenses.groupBy { it.date }
        .mapValues { (_, transactionsForDate) ->
            transactionsForDate
                .filter { it.type == UiTransactionType.Income }
                .sumOf { it.amount }
        }

    val transferWithDate = datesExpenses.groupBy { it.date }
        .mapValues { (_, transactionsForDate) ->
            transactionsForDate
                .filter { it.type == UiTransactionType.Transfer }
                .sumOf { it.amount }
        }

    val testBarParameters: List<BarParameters> = listOf(
        BarParameters(
            dataName = stringResource(MR.strings.expenses),
            data = expensesWithDate.values.toList(),
            barColor = appTheme.expenseColor,
        ),
        BarParameters(
            dataName = stringResource(MR.strings.income),
            data = incomeWithDate.values.toList(),
            barColor = appTheme.incomeColor
        ),
        BarParameters(
            dataName = stringResource(MR.strings.transfer),
            data = transferWithDate.values.toList(),
            barColor = appTheme.transferColor
        ),
    )
    Box(modifier = modifier) {
        Column {
            Text(
                stringResource(MR.strings.daily_transactions),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            BarChart(

                chartParameters = testBarParameters,
                gridColor = MaterialTheme.colorScheme.onBackground,
                xAxisData = xs,
                isShowGrid = true,
                animateChart = true,
                showGridWithSpacer = true,
                yAxisStyle = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                xAxisStyle = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.W400
                ),
            )
        }
    }
}