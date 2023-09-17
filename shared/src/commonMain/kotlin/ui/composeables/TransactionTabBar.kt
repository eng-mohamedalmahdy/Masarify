package ui.composeables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.pages.bottomnavigationpages.home.model.UiExpenseType
import ui.pages.bottomnavigationpages.home.model.getLocalisedString


@Composable
fun TransactionTabBar(
    tabItems: List<UiExpenseType>,
    selectedTab: UiExpenseType,
    onTabSelected: (UiExpenseType) -> Unit
) {

    val indicatorColor by animateColorAsState(
        targetValue = selectedTab.color,
        animationSpec = tween(300)
    )

    TabRow(
        selectedTabIndex = tabItems.indexOf(selectedTab),
        backgroundColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabItems.forEach { tab ->
            Tab(
                text = {
                    Text(
                        text = tab.getLocalisedString(),
                        fontSize = 16.sp,
                        color = if (tab == selectedTab) Color.White else Color.Black
                    )
                },
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = if (tab == selectedTab) indicatorColor else Color.Transparent,
                        shape = RoundedCornerShape(20.dp) // Adjust the corner radius as needed
                    )
                    .border(
                        width = 2.dp,
                        color = tab.color ,
                        shape = RoundedCornerShape(20.dp) // Adjust the corner radius as needed
                    )
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.Center)
            )
        }
    }
}
