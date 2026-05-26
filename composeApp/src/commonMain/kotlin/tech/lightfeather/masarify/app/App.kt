package tech.lightfeather.masarify.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import dev.icerock.moko.resources.desc.StringDesc
import io.github.vinceglb.filekit.coil.addPlatformFileSupport
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.mp.KoinPlatform
import tech.lightfeather.designsystem.component.molecules.snackbar.Snackbar
import tech.lightfeather.designsystem.component.organisms.AppAlwaysExpandedNavigationDrawer
import tech.lightfeather.designsystem.component.organisms.AppNavigationItemColors
import tech.lightfeather.designsystem.component.organisms.AppNavigationSuite
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.designsystem.util.stringResource
import tech.lightfeather.domain.model.AppLanguages
import tech.lightfeather.domain.repository.PlatformsSlugs
import tech.lightfeather.domain.repository.asSlug
import tech.lightfeather.domain.repository.getPlatform
import tech.lightfeather.masarify.auth.rememberBiometricAuthenticator
import tech.lightfeather.masarify.di.getAppModules
import tech.lightfeather.masarify.model.AppTopLevelRoutes
import tech.lightfeather.masarify.navigation.Display
import tech.lightfeather.masarify.navigation.LocalNavigator
import tech.lightfeather.masarify.navigation.NavigationRegistry
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.NavigatorImpl
import tech.lightfeather.masarify.navigation.Route
import tech.lightfeather.masarify.navigation.routes.AccountsRoute
import tech.lightfeather.masarify.navigation.routes.CategoriesRoute
import tech.lightfeather.masarify.navigation.routes.DashboardRoute
import tech.lightfeather.masarify.navigation.routes.DeleteAccountRoute
import tech.lightfeather.masarify.navigation.routes.DeleteCategoryRoute
import tech.lightfeather.masarify.navigation.routes.ForgotPasswordRoute
import tech.lightfeather.masarify.navigation.routes.LoginRoute
import tech.lightfeather.masarify.navigation.routes.MoreRoute
import tech.lightfeather.masarify.navigation.routes.OnBoardingRoute
import tech.lightfeather.masarify.navigation.routes.RegisterRoute
import tech.lightfeather.masarify.navigation.routes.ResetPasswordRoute
import tech.lightfeather.masarify.navigation.routes.SplashRoute
import tech.lightfeather.masarify.navigation.routes.TransactionsRoute
import tech.lightfeather.masarify.navigation.routes.VerifyEmailRoute
import tech.lightfeather.masarify.page.auth.forgotpassword.ForgotPasswordPage
import tech.lightfeather.masarify.page.auth.login.LoginPage
import tech.lightfeather.masarify.page.auth.register.RegisterPage
import tech.lightfeather.masarify.page.auth.resetpassword.ResetPasswordPage
import tech.lightfeather.masarify.page.auth.verifyemail.VerifyEmailPage
import tech.lightfeather.masarify.page.bankaccounts.BankAccountsPage
import tech.lightfeather.masarify.page.categories.CategoriesPage
import tech.lightfeather.masarify.page.dashboard.DashboardPage
import tech.lightfeather.masarify.page.deletebankaccount.DeleteBankAccountPage
import tech.lightfeather.masarify.page.deletecategory.DeleteCategoryPage
import tech.lightfeather.masarify.page.more.MorePage
import tech.lightfeather.masarify.page.onboarding.OnBoardingPage
import tech.lightfeather.masarify.page.splash.SplashPage
import tech.lightfeather.masarify.page.transactions.TransactionsPage
import kotlin.time.ExperimentalTime

// Main app composable with navigation setup - length is acceptable for app composition
@OptIn(ExperimentalTime::class)
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
@Preview
fun App(
    pendingDeepLink: Route? = null,
    onBackStackReady: suspend (Navigator) -> Unit = {},
) {
    // Create nav back stack with initial route
    val navBackStack =
        rememberNavBackStack(
            configuration = NavigationRegistry.savedStateConfiguration,
            SplashRoute,
        )
    val navigator = remember(navBackStack) { NavigatorImpl(navBackStack) }
    val context = LocalPlatformContext.current
    LaunchedEffect(navigator) {
        onBackStackReady(navigator)
    }
    setSingletonImageLoaderFactory {
        ImageLoader
            .Builder(context)
            .components {
                addPlatformFileSupport()
            }.build()
    }

    KoinContext(
        KoinPlatform.getKoin().apply {
            loadModules(getAppModules(navigator), allowOverride = true)
        },
    ) {
        val mainViewModel = koinViewModel<AppMainViewModel>()
        val isDarkMode by mainViewModel.darkTheme.collectAsState(false)
        val dynamicColor by mainViewModel.dynamicColor.collectAsState(false)
        val appLanguage by mainViewModel.currentLanguage.collectAsState(AppLanguages.English)

        LaunchedEffect(appLanguage) {
            StringDesc.localeType = StringDesc.LocaleType.Custom(appLanguage.code)
        }
        CompositionLocalProvider(
            LocalLayoutDirection provides if (appLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
            LocalAppMainViewModel provides mainViewModel,
            LocalNavigator provides navigator,
        ) {
            AppTheme(isDarkMode) {
                val biometricAuthenticator = rememberBiometricAuthenticator()
                val needsBiometricAuth =
                    remember(mainViewModel) {
                        mainViewModel.isBiometricEnabled() && biometricAuthenticator.isAvailable()
                    }
                var isAuthenticated by rememberSaveable { mutableStateOf(!needsBiometricAuth) }

                if (!isAuthenticated) {
                    BiometricLockScreen(
                        authenticator = biometricAuthenticator,
                        onAuthenticated = { isAuthenticated = true },
                    )
                    return@AppTheme
                }

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
                                entryStr.contains("TransactionsRoute") -> TransactionsRoute()
                                entryStr.contains("MoreRoute") -> MoreRoute
                                entryStr.contains("CategoriesRoute") -> CategoriesRoute
                                entryStr.contains("OnBoardingRoute") -> OnBoardingRoute
                                entryStr.contains("LoginRoute") -> LoginRoute
                                entryStr.contains("RegisterRoute") -> RegisterRoute
                                else -> null
                            }
                        }
                    }
                }

                val navSuiteType by remember(currentRoute) {
                    derivedStateOf {
                        with(adaptiveInfo) {
                            if (englishTopLevelRoutes.any { topLevelRoute ->
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
                                englishTopLevelRoutes.forEach { item ->
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
                                            if (englishTopLevelRoutes.any { topLevelRoute ->
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
                                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
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
                    navigator.Display(modifier = Modifier.safeDrawingPadding()) {
                        entry<SplashRoute> {
                            SplashPage(pendingRoute = pendingDeepLink)
                        }
                        entry<OnBoardingRoute> {
                            OnBoardingPage()
                        }
                        entry<DashboardRoute> {
                            DashboardPage()
                        }
                        entry<AccountsRoute> {
                            BankAccountsPage()
                        }
                        entry<TransactionsRoute> { route ->
                            TransactionsPage(
                                openAddDialog = route.openAddDialog,
                                transactionType = route.transactionType,
                                fromAccountId = route.fromAccountId,
                                categoryId = route.categoryId,
                            )
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
                        entry<LoginRoute> {
                            LoginPage()
                        }
                        entry<RegisterRoute> {
                            RegisterPage()
                        }
                        entry<VerifyEmailRoute> { route ->
                            VerifyEmailPage(token = route.token)
                        }
                        entry<ForgotPasswordRoute> {
                            ForgotPasswordPage()
                        }
                        entry<ResetPasswordRoute> { route ->
                            ResetPasswordPage(token = route.token)
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
