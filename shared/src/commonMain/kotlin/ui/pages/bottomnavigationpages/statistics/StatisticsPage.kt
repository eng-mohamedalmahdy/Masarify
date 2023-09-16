package ui.pages.bottomnavigationpages.statistics

import androidx.compose.runtime.Composable
import ui.pages.bottomnavigationpages.BottomNavigationPageModel


@Composable
fun StatisticsPage(params: BottomNavigationPageModel.StatisticsPageModel) {
    StatisticsPageViews(params)
}

@Composable
private fun StatisticsPageViews(params: BottomNavigationPageModel.StatisticsPageModel) {
    // Your compose code here
}

@Composable
private fun StatisticsPagePreview() {

    StatisticsPageViews(BottomNavigationPageModel.StatisticsPageModel)
}