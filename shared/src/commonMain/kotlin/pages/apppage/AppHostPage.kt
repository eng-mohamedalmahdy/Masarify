package pages.apppage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.P
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.decompose.router.rememberRouter
import kotlinx.coroutines.delay
import pages.BottomNavigationPage
import pages.Page
import pages.home.HomePage
import pages.more.MorePage


@Composable
fun AppHostPage() {
    AppHostPageViews()
}

@Composable
private fun AppHostPageViews() {
    val bottomNavRouter = rememberRouter(
        BottomNavigationPage::class,
        stack = listOf(BottomNavigationPage.HomePage),
    )

    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
                cutoutShape = CircleShape,
            ) {
                BottomNavigation() {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Home, "") },
                        selected = bottomNavRouter.stack.value.active.configuration == BottomNavigationPage.HomePage,
                        onClick = {
                            bottomNavRouter.replaceCurrent(BottomNavigationPage.HomePage)
                        })

                    BottomNavigationItem(
                        icon = { Icon(imageVector = Icons.Default.MoreVert, "") },
                        selected = bottomNavRouter.stack.value.active.configuration == BottomNavigationPage.MorePage,
                        onClick = {
                            bottomNavRouter.replaceCurrent(BottomNavigationPage.MorePage)
                        })
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {

                },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add icon")
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            RoutedContent(bottomNavRouter, modifier = Modifier.fillMaxSize(),) {
                when (it) {
                    BottomNavigationPage.HomePage -> HomePage()
                    BottomNavigationPage.MorePage -> MorePage()
                }
            }
        }


    }

}

@Composable
private fun AppHostPagePreview() {
// TODO: Implement call the views function here using dummy params

}

