package pages.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay


@Composable
fun SplashPage(onAnimationDone: () -> Unit) {
    SplashPageViews(onAnimationDone)
}

@Composable
private fun SplashPageViews(onAnimationDone: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(1000L)
        onAnimationDone()
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.primary)) {

        CircularProgressIndicator()
    }
}


@Composable
private fun SplashPagePreview() {

}