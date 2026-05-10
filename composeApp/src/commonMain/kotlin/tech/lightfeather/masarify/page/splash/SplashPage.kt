package tech.lightfeather.masarify.page.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.masarify.navigation.Route
import dev.icerock.moko.resources.compose.readTextAsState
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SplashPage(
    pendingRoute: Route? = null,
    viewModel: SplashPageViewModel =
        koinViewModel(
            parameters = pendingRoute?.let { route -> { parametersOf(route) } },
        ),
) {
    val state by viewModel.state.collectAsState()

    SplashPageContent()
}

@Composable
internal fun SplashPageContent() {
    val animationJson: String? by MR.assets.animated_logo_json.readTextAsState()

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(animationJson.orEmpty())
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
    )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter =
                rememberLottiePainter(
                    composition = composition,
                    progress = { progress },
                ),
            contentDescription = null,
            modifier = Modifier.size(AppTheme.dimens.massive * 4),
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    AppTheme {
        SplashPageContent()
    }
}
