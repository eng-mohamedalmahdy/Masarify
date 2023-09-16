import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.asFont
import dev.icerock.moko.resources.compose.fontFamilyResource
import di.dataSourceModule
import di.repositoryModule
import di.useCaseModule
import di.viewModelModule
import ext.navigateSingleTop
import io.github.xxfast.decompose.LocalComponentContext
import io.github.xxfast.decompose.router.Router
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.decompose.router.rememberRouter
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext
import org.koin.core.KoinApplication
import org.koin.core.context.KoinContext
import org.koin.dsl.koinApplication
import ui.pages.Page
import ui.pages.apppage.AppHostPage
import ui.pages.splash.SplashPage
import ui.style.AppTypography
import ui.style.lightModeColors

@Composable
internal fun App() {
    val lifecycle = LifecycleRegistry()
    val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)


    KoinApplication(application = {
        modules(viewModelModule, useCaseModule, repositoryModule, dataSourceModule)
    }) {
        CompositionLocalProvider(LocalComponentContext provides rootComponentContext) {

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
