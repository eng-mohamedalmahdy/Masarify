package pages.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay


@Composable
fun SplashPage(onAnimationDone: () -> Unit) {
    SplashPageViews(onAnimationDone)
}

@Composable
private fun SplashPageViews(onAnimationDone: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(3000L)
        onAnimationDone()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {

        Image(painterResource(MR.images.app_logo), "", alignment = Alignment.Center)

    }
}


@Composable
private fun SplashPagePreview() {

}