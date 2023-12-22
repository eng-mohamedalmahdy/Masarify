import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.lightfeather.masarify.MR
import dev.datlag.kcef.KCEF
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

fun main() = application {


    val windowState: WindowState = rememberWindowState()
    val rootRouterContext: RouterContext = defaultRouterContext(windowState = windowState)
    var restartRequired by remember { mutableStateOf(false) }
    var downloading by remember { mutableStateOf(0F) }
    var initialized by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(MR.strings.app_name),
        icon = painterResource(MR.images.app_logo)
    ) {
        CompositionLocalProvider(
            LocalRouterContext provides rootRouterContext,
        ) {
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    KCEF.init(builder = {
                        installDir(File("kcef-bundle"))
                        progress {
                            onDownloading {
                                downloading = max(it, 0F)
                            }
                            onInitialized {
                                initialized = true
                            }
                        }
                        settings {
                            cachePath = File("cache").absolutePath
                        }
                    }, onError = {
                        it?.printStackTrace()
                    }, onRestartRequired = {
                        restartRequired = true
                    })
                }
            }

            if (restartRequired) {
                Text(text = "Restart required.")
            } else {
                if (initialized) {
                    MainView()
                } else {
                    Text(text = "Downloading $downloading%")
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    KCEF.disposeBlocking()
                }
            }

        }
    }

}
