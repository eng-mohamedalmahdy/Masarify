import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.router.stack.push
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.decompose.router.rememberRouter
import pages.Page
import pages.apppage.AppHostPage
import pages.splash.SplashPage
import style.lightModeColors
import style.typography

@Composable
internal fun App() {
    val router = rememberRouter(Page::class, stack = listOf())

    MaterialTheme(colors = lightModeColors, typography = typography) {
        SplashPage(onAnimationDone = { router.push(Page.AppHostPage) })
        RoutedContent(router) {
            AppHostPage()
        }
    }
}
