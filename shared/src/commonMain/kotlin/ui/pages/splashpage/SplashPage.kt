package ui.pages.splashpage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.fontFamilyResource
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
        delay(500L)
        onAnimationDone()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(MR.images.app_logo), "", colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background))
            Text(
                stringResource(MR.strings.app_name),
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.background,
                style = TextStyle(
                    fontSize = 48.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = fontFamilyResource(MR.fonts.tajawal_black)
                ),
            )
            Spacer(Modifier.height(50.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
        }

    }
}


@Composable
private fun SplashPagePreview() {

}