import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import di.dataSourceModule
import di.repositoryModule
import di.useCaseModule
import ext.navigateSingleTop
import io.github.xxfast.decompose.LocalComponentContext
import io.github.xxfast.decompose.router.Router
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.decompose.router.rememberRouter
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import org.koin.compose.KoinApplication
import ui.pages.Page
import ui.pages.apppage.AppHostPage
import ui.pages.splashpage.SplashPage
import ui.style.AppTypography
import ui.style.lightModeColors

@Composable
internal fun App() {
    val lifecycle = LifecycleRegistry()
    val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)


    KoinApplication(application = {
        modules(useCaseModule, repositoryModule, dataSourceModule)
    }) {
        CompositionLocalProvider(
            LocalComponentContext provides rootComponentContext,
        ) {


            MaterialTheme(colors = lightModeColors, typography = AppTypography()) {

                val router: Router<Page> = rememberRouter(type = Page::class, stack = listOf(Page.SplashPage))

                RoutedContent(router, modifier = Modifier.fillMaxSize()) {
                    when (it) {
                        Page.AppHostPage -> AppHostPage()
                        Page.SplashPage -> SplashPage(onAnimationDone = {
                            router.navigateSingleTop { Page.AppHostPage }
                        })
                    }
                }
            }
        }
    }

}
