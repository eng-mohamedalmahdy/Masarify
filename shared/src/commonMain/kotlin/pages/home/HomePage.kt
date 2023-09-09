package pages.home

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun HomePage() {
    HomePageViews()
}

@Composable
private fun HomePageViews() {
    Text("Home Page", color = MaterialTheme.colors.onBackground)
}

@Composable
private fun HomePagePreview() {
// TODO: Implement call the views function here using dummy params

}