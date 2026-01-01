package com.lightfeather.masarify.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.window.core.layout.WindowWidthSizeClass
import com.lightfeather.designsystem.component.molecules.snackbar.Snackbar
import com.lightfeather.designsystem.component.organisms.AppAlwaysExpandedNavigationDrawer
import com.lightfeather.designsystem.component.organisms.AppNavigationItemColors
import com.lightfeather.designsystem.component.organisms.AppNavigationSuite
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.stringResource
import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.masarify.PlatformsSlugs
import com.lightfeather.masarify.asSlug
import com.lightfeather.masarify.di.getAppModules
import com.lightfeather.masarify.getPlatform
import com.lightfeather.masarify.model.AppTopLevelRoutes
import com.lightfeather.masarify.navigation.Display
import com.lightfeather.masarify.navigation.LocalNavigator
import com.lightfeather.masarify.navigation.NavigationRegistry
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.NavigatorImpl
import com.lightfeather.masarify.navigation.routes.AccountsRoute
import com.lightfeather.masarify.navigation.routes.CategoriesRoute
import com.lightfeather.masarify.navigation.routes.DashboardRoute
import com.lightfeather.masarify.navigation.routes.DeleteAccountRoute
import com.lightfeather.masarify.navigation.routes.DeleteCategoryRoute
import com.lightfeather.masarify.navigation.routes.MoreRoute
import com.lightfeather.masarify.navigation.routes.OnBoardingRoute
import com.lightfeather.masarify.navigation.routes.SplashRoute
import com.lightfeather.masarify.navigation.routes.TransactionsRoute
import com.lightfeather.masarify.page.bankaccounts.BankAccountsPage
import com.lightfeather.masarify.page.categories.CategoriesPage
import com.lightfeather.masarify.page.deletebankaccount.DeleteBankAccountPage
import com.lightfeather.masarify.page.deletecategory.DeleteCategoryPage
import com.lightfeather.masarify.page.more.MorePage
import com.lightfeather.masarify.page.onboarding.OnBoardingPage
import com.lightfeather.masarify.page.splash.SplashPage
import dev.icerock.moko.resources.desc.StringDesc
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.mp.KoinPlatform

// Main app composable with navigation setup - length is acceptable for app composition
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
@Preview
fun App(onBackStackReady: suspend (Navigator) -> Unit = {}) {
    // Create nav back stack with initial route
    val navBackStack =
        rememberNavBackStack(
            configuration = NavigationRegistry.savedStateConfiguration,
            SplashRoute,
        )
    val navigator = remember(navBackStack) { NavigatorImpl(navBackStack) }

    LaunchedEffect(navigator) {
        onBackStackReady(navigator)
    }

    KoinContext(
        KoinPlatform.getKoin().apply {
            loadModules(getAppModules(navigator))
        },
    ) {
        val mainViewModel = koinViewModel<AppMainViewModel>()
        val isDarkMode by mainViewModel.darkTheme.collectAsState(false)
        val dynamicColor by mainViewModel.dynamicColor.collectAsState(false)
        val appLanguage by mainViewModel.currentLanguage.collectAsState(AppLanguage.English)

        LaunchedEffect(appLanguage) {
            StringDesc.localeType = StringDesc.LocaleType.Custom(appLanguage.code)
        }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (appLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            LocalAppMainViewModel provides mainViewModel,
            LocalNavigator provides navigator,
        ) {
            AppTheme(isDarkMode) {
                val englishTopLevelRoutes =
                    listOf<AppTopLevelRoutes>(
                        AppTopLevelRoutes.Dashboard,
                        AppTopLevelRoutes.Transactions,
                        AppTopLevelRoutes.Accounts,
                        AppTopLevelRoutes.More,
                    )

                // Reserved for future RTL support with different route ordering
                @Suppress("UnusedPrivateProperty")
                val arabicTopLevelRoutes =
                    listOf<AppTopLevelRoutes>(
                        AppTopLevelRoutes.Dashboard,
                        AppTopLevelRoutes.Transactions,
                        AppTopLevelRoutes.Accounts,
                        AppTopLevelRoutes.More,
                    )
                val topLevelRoutes = englishTopLevelRoutes
                val adaptiveInfo = currentWindowAdaptiveInfo()
                val currentRoute by remember {
                    derivedStateOf {
                        navigator.backStack.lastOrNull()?.let { entry ->
                            // Extract route from entry string representation
                            val entryStr = entry.toString()
                            when {
                                entryStr.contains("SplashRoute") -> SplashRoute
                                entryStr.contains("DashboardRoute") -> DashboardRoute
                                entryStr.contains("AccountsRoute") -> AccountsRoute
                                entryStr.contains("TransactionsRoute") -> TransactionsRoute
                                entryStr.contains("MoreRoute") -> MoreRoute
                                entryStr.contains("CategoriesRoute") -> CategoriesRoute
                                entryStr.contains("OnBoardingRoute") -> OnBoardingRoute
                                else -> null
                            }
                        }
                    }
                }

                val navSuiteType by remember(currentRoute) {
                    derivedStateOf {
                        with(adaptiveInfo) {
                            if (topLevelRoutes.any { topLevelRoute ->
                                    currentRoute?.routeName == topLevelRoute.route.routeName
                                }
                            ) {
                                when (getPlatform().asSlug()) {
                                    PlatformsSlugs.WEB if (
                                        adaptiveInfo.windowSizeClass.windowWidthSizeClass ==
                                            WindowWidthSizeClass.EXPANDED
                                        ) -> {
                                        NavigationSuiteType.NavigationDrawer
                                    }

                                    else -> NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(this)
                                }
                            } else {
                                NavigationSuiteType.None
                            }
                        }
                    }
                }

                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .background(MaterialTheme.colorScheme.background),
                ) {}
                NavigationSuiteScaffoldLayout(
                    navigationSuite = {
                        AppNavigationSuite(
                            navigationSuiteType = navSuiteType,
                            builder = {
                                set(
                                    NavigationSuiteType.NavigationDrawer,
                                ) { items, primaryActionContent, verticalArrangement, colors ->
                                    AppAlwaysExpandedNavigationDrawer(
                                        items = items,
                                        primaryActionContent = primaryActionContent,
                                        verticalArrangement = verticalArrangement,
                                        colors = colors,
                                    )
                                }
                            },
                            navigationSuiteColors =
                                NavigationSuiteDefaults.colors(
                                    navigationDrawerContainerColor =
                                        if (isDarkMode) {
                                            MaterialTheme.colorScheme.surface
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        },
                                    navigationDrawerContentColor =
                                        if (isDarkMode) {
                                            MaterialTheme.colorScheme.onSurface
                                        } else {
                                            MaterialTheme.colorScheme.onPrimary
                                        },
                                    shortNavigationBarContainerColor = MaterialTheme.colorScheme.primary,
                                    shortNavigationBarContentColor = MaterialTheme.colorScheme.onPrimary,
                                    navigationRailContainerColor =
                                        if (isDarkMode) {
                                            MaterialTheme.colorScheme.surface
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        },
                                    navigationRailContentColor =
                                        if (isDarkMode) {
                                            MaterialTheme.colorScheme.onSurface
                                        } else {
                                            MaterialTheme.colorScheme.onPrimary
                                        },
                                ),
                            content = {
                                topLevelRoutes.forEach { item ->
                                    val isSelected by remember(currentRoute) {
                                        derivedStateOf {
                                            currentRoute?.routeName == item.route.routeName
                                        }
                                    }
                                    item(
                                        icon = {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = stringResource(item.label),
                                            )
                                        },
                                        label = {
                                            Text(
                                                stringResource(item.label).orEmpty(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            )
                                        },
                                        selected = isSelected,
                                        onClick = {
                                            if (topLevelRoutes.any { topLevelRoute ->
                                                    currentRoute?.routeName == topLevelRoute.route.routeName
                                                }
                                            ) {
                                                // Already on a top level route, clear back stack and navigate
                                                navigator.navigateAndClearBackStack(item.route)
                                            } else {
                                                // Not on a top level route, just navigate
                                                navigator.navigate(item.route)
                                            }
                                        },
                                        colors =
                                            AppNavigationItemColors.defaultColors().copy(
                                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                                unselectedIconColor =
                                                    MaterialTheme.colorScheme.onPrimary.copy(
                                                        alpha = 0.7f,
                                                    ),
                                                selectedTextColor =
                                                    if (navSuiteType == NavigationSuiteType.NavigationDrawer) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onPrimary
                                                    },
                                                unselectedTextColor =
                                                    if (isDarkMode) {
                                                        MaterialTheme.colorScheme.surface
                                                    } else {
                                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                                    },
                                                selectedContainerColor = MaterialTheme.colorScheme.surface,
                                                unselectedContainerColor = Color.Transparent,
                                                indicatorColor = MaterialTheme.colorScheme.onPrimary,
                                            ),
                                    )
                                }
                            },
                        )
                    },
                    layoutType = navSuiteType,
                ) {
                    navigator.Display(modifier = Modifier) {
                        entry<SplashRoute> {
                            SplashPage()
                        }
                        entry<OnBoardingRoute> {
                            OnBoardingPage()
                        }
                        entry<DashboardRoute> {
                            // Dashboard placeholder
                            Text("Dashboard Page - Coming Soon")
                        }
                        entry<AccountsRoute> {
                            BankAccountsPage()
                        }
                        entry<TransactionsRoute> {
                            Text("Transactions Page")
                        }
                        entry<MoreRoute> {
                            MorePage()
                        }
                        entry<CategoriesRoute> {
                            CategoriesPage()
                        }
                        entry<DeleteAccountRoute> { route ->
                            DeleteBankAccountPage(account = route.account)
                        }
                        entry<DeleteCategoryRoute> { route ->
                            DeleteCategoryPage(category = route.category)
                        }
                    }
                }
                Snackbar()
            }
        }
    }
}

val LocalAppMainViewModel =
    staticCompositionLocalOf<AppMainViewModel> {
        error("No ViewModel provided")
    }
