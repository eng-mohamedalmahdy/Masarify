package ui.pages.bottomnavigationpages.more

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ui.pages.bottomnavigationpages.BottomNavigationPageModel


@Composable
fun MorePage(params: BottomNavigationPageModel.MorePageModel) {
    MorePageViews(params)
}

@Composable
private fun MorePageViews(params: BottomNavigationPageModel.MorePageModel) {
    Text("More", color = MaterialTheme.colorScheme.onBackground)
}

@Composable
private fun MorePagePreview() {

    MorePageViews(BottomNavigationPageModel.MorePageModel)
}