package ui.pages.bottomnavigationpages.statistics.view

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ext.toComposeColor
import ui.entity.UiExpenseCategory
import ui.main.LocalAppTheme

@Composable
fun CategoriesPieChart(
    title: String,
    list: List<Pair<UiExpenseCategory, Double>>,
    modifier: Modifier = Modifier
) {
    val appTheme = LocalAppTheme.current

    val categories = list.map {
        PieChartData(
            partName = it.first.name,
            data = it.second,
            color = it.first.color.toComposeColor(),
        )
    }

    Column(modifier) {
        Text(
            title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )

        if (categories.isNotEmpty()) {
            // Display PieChart when there is data
            PieChart(
                animation =  TweenSpec(durationMillis = 0),
                modifier = Modifier.fillMaxSize(),
                pieChartData = categories,
                ratioLineColor = Color.LightGray,
                textRatioStyle = TextStyle(color = Color.Gray),
            )
        } else {
            // Display a placeholder or message when there is no data
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(MR.strings.no_income_or_expenses_were_added),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }


}