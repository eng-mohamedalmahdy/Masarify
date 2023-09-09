package pages.more

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable


@Composable
fun MorePage() {
    MorePageViews()
}

@Composable
private fun MorePageViews() {
    Text("More", color = MaterialTheme.colors.onBackground)
}

@Composable
private fun MorePagePreview() {
// TODO: Implement call the views function here using dummy params

}