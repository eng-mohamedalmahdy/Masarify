import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext

fun main() = application {

    val windowState: WindowState = rememberWindowState()
    val rootRouterContext: RouterContext = defaultRouterContext(windowState = windowState)

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(MR.strings.app_name),
        icon = painterResource(MR.images.app_logo)
    ) {
        CompositionLocalProvider(
            LocalRouterContext provides rootRouterContext,
        ) { MainView() }
    }
}