import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.PredictiveBackGestureIcon
import com.arkivanov.decompose.extensions.compose.jetbrains.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import di.getBaseModules
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import org.koin.compose.KoinApplication

@OptIn(ExperimentalDecomposeApi::class)
fun MainViewController(routerContext: RouterContext) = ComposeUIViewController {
    CompositionLocalProvider(
        LocalRouterContext provides routerContext,
    ) {
        MaterialTheme {
            PredictiveBackGestureOverlay(
                backDispatcher = routerContext.backHandler as BackDispatcher, // Use the same BackDispatcher as above
                backIcon = { progress, _ ->
                    PredictiveBackGestureIcon(
                        imageVector = Icons.Default.ArrowBack,
                        progress = progress,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            )
            {
                KoinApplication(application = {

                    modules(
                        getBaseModules()
                    )
                })
                {
                    App()
                }
            }
        }
    }
}