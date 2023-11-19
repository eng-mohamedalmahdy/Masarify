import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import di.databaseDriverFactoryModule
import di.databaseQueriesModule
import di.dummyDataSourceModule
import di.getBaseModules
import di.localDatasourceModule
import di.repositoryModule
import di.useCaseModule
import ext.navigateSingleTop
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.Router
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.decompose.router.rememberRouter
import io.ktor.util.Platform
import io.ktor.util.PlatformUtils
import org.koin.compose.KoinApplication
import ui.pages.Page
import ui.pages.apppage.AppHostPage
import ui.pages.createbankaccount.CreateBankAccountPage
import ui.pages.splashpage.SplashPage
import ui.style.AppTheme.AppTypography
import ui.style.AppTheme.lightModeColors

@Composable
internal fun App() {

    Napier.base(DebugAntilog())


//    KoinApplication(application = {
//
//        modules(
//            getBaseModules()
//        )
//    }) {
        CompositionLocalProvider() {
            MaterialTheme(colorScheme = lightModeColors, typography = AppTypography) {
                val router: Router<Page> =
                    rememberRouter(type = Page::class, initialStack = { listOf(Page.SplashPage) })
                CompositionLocalProvider(LocalMainNavController provides router) {
                    RoutedContent(router, modifier = Modifier.fillMaxSize()) {
                        Napier.d("$it")
                        when (it) {
                            Page.AppHostPage -> AppHostPage()
                            Page.SplashPage -> SplashPage(onAnimationDone = {
                                router.navigateSingleTop { Page.AppHostPage }
                            })

                            Page.CreateBankAccountPageModel -> CreateBankAccountPage()
                        }
                    }
                }
//                TestPage()
            }
        }
    }

//}

val LocalMainNavController = compositionLocalOf<Router<Page>> {
    error("No NavController provided")
}

