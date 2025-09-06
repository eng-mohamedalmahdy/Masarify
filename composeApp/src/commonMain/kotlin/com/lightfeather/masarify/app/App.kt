package com.lightfeather.masarify.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lightfeather.designsystem.component.snackbar.Snackbar
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.stringResource
import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.masarify.di.getAppModules
import com.lightfeather.masarify.model.AppTopLevelRoutes
import com.lightfeather.masarify.navigation.routes.DashboardRoute
import com.lightfeather.masarify.navigation.routes.OnBoardingRoute
import com.lightfeather.masarify.pages.onboarding.OnBoardingPage
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.mp.KoinPlatform

@Composable
@Preview
fun App(
    onNavHostReady: suspend (NavController) -> Unit = {}
) {
    val navController = rememberNavController()
    KoinContext(
        KoinPlatform.getKoin().apply {
            loadModules(getAppModules(navController))
        }
    ) {


        val mainViewModel = koinViewModel<AppMainViewModel>()
        val isDarkMode by mainViewModel.darkTheme.collectAsState(false)
        val dynamicColor by mainViewModel.dynamicColor.collectAsState(false)
        val appLanguage by mainViewModel.currentLanguage.collectAsState(AppLanguage.English)
        val initialDataSaved by mainViewModel.initialDataSaved.collectAsState(false)
        LaunchedEffect(appLanguage) {
            StringDesc.localeType = StringDesc.LocaleType.Custom(appLanguage.code)
        }
        LaunchedEffect(navController) {
            onNavHostReady(navController)
        }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (appLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            AppTheme(isDarkMode, dynamicColor) {
                LaunchedEffect(initialDataSaved) {
                    Napier.d("Initial Data Saved: $initialDataSaved")
                }
                val startDestination = if (initialDataSaved) DashboardRoute else OnBoardingRoute()


                val englishTopLevelRoutes = listOf<AppTopLevelRoutes>(
                    AppTopLevelRoutes.Dashboard,
                )
                val arabicTopLevelRoutes = listOf<AppTopLevelRoutes>(
                    AppTopLevelRoutes.Dashboard,
                )
                val topLevelRoutes = if (appLanguage.isRtl) arabicTopLevelRoutes.reversed() else englishTopLevelRoutes
                val adaptiveInfo = currentWindowAdaptiveInfo()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination by remember(navBackStackEntry) { derivedStateOf { navBackStackEntry?.destination } }

                val navSuiteType by remember(navBackStackEntry) {
                    derivedStateOf {
                        with(adaptiveInfo) {
                            if (
                                topLevelRoutes.any { topLevelRoute -> currentDestination?.hasRoute(topLevelRoute.route::class) == true }) {
                                NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(this)
                            } else {
                                NavigationSuiteType.None

                            }
                        }
                    }
                }
                NavigationSuiteScaffold(
                    navigationSuiteItems = {
                        topLevelRoutes.forEach { item ->
                            item(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = stringResource(item.label)
                                    )
                                },
                                label = { Text(stringResource(item.label).orEmpty()) },
                                selected = currentDestination!!.hierarchy.any {
                                    it.hasRoute(item.route::class)
                                },
                                onClick = {
                                    if (topLevelRoutes.any { topLevelRoute ->
                                            currentDestination!!.hasRoute(topLevelRoute.route::class)
                                        }) {
                                        navController.navigate(item.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().route.orEmpty()) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    },
                    layoutType = navSuiteType,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier
                    ) {
                        composable<DashboardRoute> {
                            val getAllAccounts = koinInject<GetAllAccounts>()
                            LaunchedEffect(Unit){
                              getAllAccounts().foldSuspend(
                                  onSuccess = {
                                      it.collect {
                                          Napier.d("In get all Accounts: $it")
                                      }
                                  },
                                  onFailure = {
                                      // Handle failure
                                  }
                              )
                            }
                            Text("Masarify App")
                        }
                        composable<OnBoardingRoute> {
                            OnBoardingPage()
                        }
                    }
                }
                Snackbar()
            }
        }

    }
}