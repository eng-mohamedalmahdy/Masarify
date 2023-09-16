package ui.pages.bottomnavigationpages.more

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import ui.pages.bottomnavigationpages.BottomNavigationPageModel


@Composable
fun MorePage(params: BottomNavigationPageModel.MorePageModel) {
    MorePageViews(params)
}

@Composable
private fun MorePageViews(params: BottomNavigationPageModel.MorePageModel) {
    Text("More", color = MaterialTheme.colors.onBackground)
}

@Composable
private fun MorePagePreview() {

    MorePageViews(BottomNavigationPageModel.MorePageModel)
}