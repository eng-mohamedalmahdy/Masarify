package com.lightfeather.masarify.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lightfeather.designsystem.component.AppBottomNavigation
import com.lightfeather.designsystem.theme.AppTheme.AppTheme
import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.masarify.di.getAppModules
import com.lightfeather.masarify.model.AppTopLevelRoutes
import com.lightfeather.masarify.navigation.routes.HomeRoute
import com.lightfeather.masarify.pages.onboarding.OnBoardingPage
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
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
                val englishTopLevelRoutes = listOf<AppTopLevelRoutes>()
                val arabicTopLevelRoutes = listOf<AppTopLevelRoutes>()
                val topLevelRoutes = if (appLanguage.isRtl) arabicTopLevelRoutes.reversed() else englishTopLevelRoutes
                Scaffold(
                    bottomBar = {
                        val navBackStackEntry =
                            navController.currentBackStackEntryAsState().value
                        val currentDestination = navBackStackEntry?.destination
                        if (currentDestination != null && topLevelRoutes.any { topLevelRoute ->
                                currentDestination.hasRoute(
                                    topLevelRoute.route::class
                                )
                            }) {
                            AppBottomNavigation(
                                navItems = topLevelRoutes,
                                isCurrentDestination = { item ->
                                    val item = item as AppTopLevelRoutes
                                    currentDestination.hierarchy.any { it.hasRoute(item.route::class) }
                                },
                                onItemClick = { navigationItem ->
                                    val route = navigationItem as AppTopLevelRoutes
                                    if (topLevelRoutes.any { topLevelRoute ->
                                            currentDestination.hasRoute(topLevelRoute.route::class)
                                        }) {
                                        navController.navigate(route.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate(route.route) {
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
                    modifier = Modifier.fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .background(MaterialTheme.colorScheme.background)
                ) { innerPadding ->
                    val layoutDirection = LocalLayoutDirection.current

                    NavHost(
                        navController,
                        startDestination = HomeRoute,
                        modifier = Modifier
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = (innerPadding.calculateBottomPadding() - 16.dp).coerceAtLeast(0.dp),
                                start = innerPadding.calculateStartPadding(layoutDirection),
                                end = innerPadding.calculateEndPadding(layoutDirection)
                            )
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        composable<HomeRoute> {
                            OnBoardingPage()
                        }
                    }

                }
            }
        }

    }
}