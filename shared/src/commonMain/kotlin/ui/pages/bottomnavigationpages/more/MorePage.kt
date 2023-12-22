package ui.pages.bottomnavigationpages.more

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.stack.pop
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ext.navigateSingleTop
import ui.composeables.DropDownTextField
import ui.composeables.MoreListItem
import ui.composeables.ThemeSwitcher
import ui.main.LocalBottomNavigationNavController
import ui.main.LocalMainNavController
import ui.main.LocalMainViewModel
import ui.pages.Page


@Composable
fun MorePage() {
    val mainViewModel = LocalMainViewModel.current
    val isDarkMode by mainViewModel.isSystemDarkMode.collectAsState()
    val currentLanguage by mainViewModel.currentLanguage.collectAsState()
    val languagesList = listOf("English" to "en", "العربية" to "ar")
    val mainRouter = LocalMainNavController.current
    val bottomRouter = LocalBottomNavigationNavController.current
    val moreItems = listOf(
        Triple(
            Icons.Default.Policy,
            stringResource(MR.strings.privacy_policy)
        ) { mainRouter.navigateSingleTop { Page.WebViewPage("https://sites.google.com/view/masarify/privacy-policy") } },
        Triple(
            Icons.Default.ContactSupport,
            stringResource(MR.strings.contact_us)
        ) { mainRouter.navigateSingleTop { Page.WebViewPage("https://sites.google.com/view/masarify/contact-us") } },
        Triple(
            Icons.Default.Info,
            stringResource(MR.strings.about_us)
        ) { mainRouter.navigateSingleTop { Page.WebViewPage("https://sites.google.com/view/masarify") } }
    )
    LazyColumn(Modifier.padding(16.dp)) {
        item {
            Spacer(Modifier.height(12.dp))
        }
        item {
            Text(
                stringResource(MR.strings.settings),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
                ThemeSwitcher(isDarkMode) {
                    mainViewModel.setDarkMode(!isDarkMode)
                }
            }
        }
        item {
            DropDownTextField(
                languagesList.first { currentLanguage == it.second }.first,
                languagesList.map { it.first },
                { Text(stringResource(MR.strings.language)) },
                { idx, item ->
                    mainViewModel.setCurrentLanguage(languagesList[idx].second)
                    bottomRouter.pop()
                },
                Modifier.padding(8.dp)
            )
        }
        item {
            Spacer(Modifier.height(12.dp))
        }
        item {
            Text(
                stringResource(MR.strings.more_options),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(
            moreItems
        ) { (icon, title, onClick) ->
            MoreListItem(icon, title, onClick)
        }
    }
}


