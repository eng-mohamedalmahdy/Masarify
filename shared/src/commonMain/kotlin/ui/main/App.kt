package ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.russhwolf.settings.Settings
import data.local.isFirstTime
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.xxfast.decompose.router.Router
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.decompose.router.rememberRouter
import ui.pages.Page
import ui.pages.apppage.AppHostPage
import ui.pages.bottomnavigationpages.BottomNavigationPageModel
import ui.pages.createbankaccount.CreateBankAccountPage
import ui.pages.splashpage.SplashPage
import ui.pages.userprimarydata.FillUserPrimaryDataPage
import ui.pages.webviewpage.WebViewPage
import ui.style.AppDarkTheme
import ui.style.AppLightTheme
import ui.style.AppTheme
import ui.util.asSnakeBarAction
import ui.util.color

@Composable
internal fun App() {

    Napier.base(DebugAntilog())

    val viewModel = MainViewModel().let { getViewModel(Unit, viewModelFactory { it }) }
    val viewModelRemembered = remember { viewModel }
    val settings = Settings()

    val isDarkMode by viewModelRemembered.isSystemDarkMode.collectAsState()
    val language by viewModelRemembered.currentLanguage.collectAsState()

    val appTheme: AppTheme = if (isDarkMode) AppDarkTheme else AppLightTheme

    LaunchedEffect(language) {

        StringDesc.localeType = StringDesc.LocaleType.Custom(language)

    }

    MaterialTheme(colorScheme = appTheme.colorScheme, typography = appTheme.typography) {

        val router: Router<Page> =
            rememberRouter(type = Page::class, initialStack = { listOf(Page.SplashPage) })
        val bottomNavRouter = rememberRouter(
            BottomNavigationPageModel::class,
            initialStack = { listOf(BottomNavigationPageModel.HomePageModel) },
        )
        val snackBarHostState = remember { SnackbarHostState() }


        CompositionLocalProvider(
            LocalLocale provides language,
            LocalMainViewModel provides viewModelRemembered,
            LocalAppTheme provides appTheme,
            LocalMainNavController provides router,
            LocalBottomNavigationNavController provides bottomNavRouter,
            LocalSettings provides settings,
            LocalSnackBarHostState provides snackBarHostState
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        snackBarHostState,
                        Modifier.padding(16.dp)
                    ) {
                        val action = it.visuals.actionLabel?.asSnakeBarAction()
                        val snackColor = action?.color
                        Snackbar(
                            containerColor = snackColor ?: MaterialTheme.colorScheme.onBackground
                        ) {
                            Text(
                                it.visuals.message,
                                modifier = Modifier.padding(8.dp),
                                color = MaterialTheme.colorScheme.background,
                                fontSize = 18.sp
                            )
                        }
                    }
                },
            ) {
                RoutedContent(router, modifier = Modifier.fillMaxSize().padding(it)) {
                    when (it) {
                        Page.AppHostPage -> AppHostPage()
                        Page.SplashPage -> SplashPage(onAnimationDone = {
                            if (settings.isFirstTime()) {
                                router.replaceCurrent(Page.FillUserPrimaryDataPageModel)
                            } else {
                                router.replaceCurrent(Page.AppHostPage)
                            }
                        })

                        Page.CreateBankAccountPageModel -> CreateBankAccountPage()
                        Page.FillUserPrimaryDataPageModel -> FillUserPrimaryDataPage()
                        is Page.WebViewPage -> WebViewPage(it.url)
                    }
                }
            }
        }
    }
}


val LocalMainNavController = compositionLocalOf<Router<Page>> {
    error("No NavController provided")
}

val LocalBottomNavigationNavController = compositionLocalOf<Router<BottomNavigationPageModel>> {
    error("No NavController provided")
}


val LocalSettings = compositionLocalOf<Settings> {
    error("No Settings provided")
}

val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> {
    error("No Host Provided")
}

val LocalAppTheme = compositionLocalOf<AppTheme> {
    error("No App Theme provided")
}

val LocalMainViewModel = compositionLocalOf<MainViewModel> {
    error("No View Model Provided")
}

val LocalLocale = compositionLocalOf<String> {
    error("No Locale Provided")
}