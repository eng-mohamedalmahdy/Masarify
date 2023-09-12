import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.painterResource
import io.github.xxfast.decompose.LocalComponentContext
import io.github.xxfast.decompose.router.Router
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.decompose.router.rememberRouter
import pages.Page
import pages.apppage.AppHostPage
import pages.splash.SplashPage
import style.lightModeColors
import style.typography

@Composable
internal fun App() {
    val lifecycle = LifecycleRegistry()
    val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)


    CompositionLocalProvider(LocalComponentContext provides rootComponentContext) {

        MaterialTheme(colors = lightModeColors, typography = typography) {

            val router: Router<Page> = rememberRouter(type = Page::class, stack = listOf(Page.SplashPage))

            RoutedContent(router, modifier = Modifier.fillMaxSize()) {
                when (it) {
                    Page.AppHostPage -> AppHostPage()
                    Page.SplashPage -> SplashPage(onAnimationDone = {
                        router.pop()
                        router.push(Page.AppHostPage)
                    })
                }
            }
        }
    }
}
