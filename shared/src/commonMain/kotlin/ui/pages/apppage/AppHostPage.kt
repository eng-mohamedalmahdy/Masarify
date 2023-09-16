package ui.pages.apppage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import ext.navigateSingleTop
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.decompose.router.rememberRouter
import ui.pages.bottomnavigationpages.BottomNavigationPageModel
import ui.pages.bottomnavigationpages.bankaccounts.BankAccountsPage
import ui.pages.bottomnavigationpages.currencyconverter.CurrencyConverterPage
import ui.pages.bottomnavigationpages.home.HomePage
import ui.pages.bottomnavigationpages.more.MorePage
import ui.pages.bottomnavigationpages.statistics.StatisticsPage


@Composable
fun AppHostPage() {
    AppHostPageViews()
}

@Composable
private fun AppHostPageViews() {

    val bottomNavRouter = rememberRouter(
        BottomNavigationPageModel::class,
        stack = listOf(BottomNavigationPageModel.HomePageModel),
    )


    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
                cutoutShape = CircleShape,
                contentPadding = PaddingValues(8.dp)
            ) {
                val activeTab = bottomNavRouter.stack.value.active.configuration


                BottomNavigation(elevation = 0.dp) {
                    BottomNavigationItem(
                        icon = {
                            AppBottomNavigationItem(
                                painterResource(MR.images.menu),
                                stringResource(MR.strings.more)
                            )
                        },
                        selected = activeTab == BottomNavigationPageModel.MorePageModel,
                        onClick = { bottomNavRouter.navigateSingleTop { BottomNavigationPageModel.MorePageModel } },
                        modifier = Modifier.weight(1f)
                    )

                    BottomNavigationItem(
                        icon = {
                            AppBottomNavigationItem(
                                painterResource(MR.images.bar_chart),
                                stringResource(MR.strings.statistics)
                            )
                        },
                        selected = activeTab == BottomNavigationPageModel.StatisticsPageModel,
                        onClick = { bottomNavRouter.navigateSingleTop { BottomNavigationPageModel.StatisticsPageModel } },
                        modifier = Modifier.weight(1f)

                    )
                    Spacer(modifier = Modifier.weight(1f))
                    BottomNavigationItem(
                        icon = {
                            AppBottomNavigationItem(
                                painterResource(MR.images.currency_converter),
                                stringResource(MR.strings.currency_converter)
                            )
                        },
                        selected = activeTab == BottomNavigationPageModel.CurrencyConverterPageModel,
                        onClick = { bottomNavRouter.navigateSingleTop { BottomNavigationPageModel.CurrencyConverterPageModel } },
                        modifier = Modifier.weight(1f)

                    )
                    BottomNavigationItem(
                        icon = {
                            AppBottomNavigationItem(
                                painterResource(MR.images.bank_account),
                                stringResource(MR.strings.bank_accounts)
                            )
                        },
                        selected = activeTab == BottomNavigationPageModel.BankAccountsPageModel,
                        onClick = { bottomNavRouter.navigateSingleTop { BottomNavigationPageModel.BankAccountsPageModel } },
                        modifier = Modifier.weight(1f)

                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    bottomNavRouter.navigateSingleTop {
                        BottomNavigationPageModel.HomePageModel
                    }
                },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(MR.strings.home))
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            RoutedContent(bottomNavRouter, modifier = Modifier.fillMaxSize()) {
                when (it) {
                    is BottomNavigationPageModel.HomePageModel -> HomePage()
                    is BottomNavigationPageModel.MorePageModel -> MorePage(it)
                    is BottomNavigationPageModel.BankAccountsPageModel -> BankAccountsPage(it)
                    is BottomNavigationPageModel.CurrencyConverterPageModel -> CurrencyConverterPage(it)
                    is BottomNavigationPageModel.StatisticsPageModel -> StatisticsPage(it)
                }
            }
        }


    }

}

@Composable
private fun AppBottomNavigationItem(icon: Painter, label: String) {
    Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colors.secondary)
        Text(label, color = MaterialTheme.colors.onPrimary)
    }
}

@Composable
private fun AppHostPagePreview() {
// TODO: Implement call the views function here using dummy params

}

