package ui.composeables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import ui.pages.bottomnavigationpages.home.model.UiExpenseType
import ui.pages.bottomnavigationpages.home.model.getLocalisedString
import ui.style.AppTheme


@Composable
fun TransactionTabBar(
    tabItems: List<UiExpenseType>,
    selectedTab: UiExpenseType,
    onTabSelected: (UiExpenseType) -> Unit
) {


    TabRow(
        selectedTabIndex = tabItems.indexOf(selectedTab),
        containerColor = AppTheme.cardColor,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 30.dp)
            .background(AppTheme.cardColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        indicator = {},
        divider = {}
    ) { tabItems.forEach { tab -> TabItem(tab, tab == selectedTab, onTabSelected) } }
}

@Composable
private fun TabItem(expenseType: UiExpenseType, isSelected: Boolean, onTabSelected: (UiExpenseType) -> Unit) {
    val iconTint = expenseType.color
    val borderColor = if (isSelected) expenseType.color else Color.Transparent
    val backgroundColor = if (isSelected) Color.White else expenseType.color.copy(alpha = .25f)
    val textColor = if (isSelected) expenseType.color else Color.Black
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            painter = painterResource(MR.images.app_logo),
            expenseType.getLocalisedString(),
            Modifier.size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = { onTabSelected(expenseType) }
                )
                .background(backgroundColor, RoundedCornerShape(16.dp))
                .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(16.dp))
                .padding(16.dp)
                ,
            colorFilter = ColorFilter.tint(iconTint)
        )
        Spacer(Modifier.height(16.dp))
        Text(expenseType.getLocalisedString(), color = textColor)
    }
}
