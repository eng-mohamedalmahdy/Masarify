package ui.composeables

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.painterResource
import ui.main.LocalAppTheme
import ui.pages.bottomnavigationpages.home.model.UiTransactionType
import ui.pages.bottomnavigationpages.home.model.getLocalisedString


@Composable
fun TransactionTabBar(
    tabItems: List<UiTransactionType>,
    selectedTab: UiTransactionType,
    onTabSelected: (UiTransactionType) -> Unit
) {


    TabRow(
        selectedTabIndex = tabItems.indexOf(selectedTab),
        containerColor = LocalAppTheme.current.cardColor,
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
            .background(LocalAppTheme.current.cardColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        indicator = {},
        divider = {}
    ) { tabItems.forEach { tab -> TabItem(tab, tab == selectedTab, onTabSelected) } }
}

@Composable
private fun TabItem(expenseType: UiTransactionType, isSelected: Boolean, onTabSelected: (UiTransactionType) -> Unit) {
    val iconTint = expenseType.color
    val borderColor = if (isSelected) expenseType.color else Color.Transparent
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.background else expenseType.color.copy(alpha = .25f)
    val textColor = if (isSelected) expenseType.color else MaterialTheme.colorScheme.onBackground
    val fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            painter = painterResource(MR.images.app_logo),
            expenseType.getLocalisedString(),
            Modifier.size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = { onTabSelected(expenseType) }
                )
                .background(backgroundColor, RoundedCornerShape(16.dp))
                .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(16.dp))
                .padding(16.dp),
            colorFilter = ColorFilter.tint(iconTint)
        )
        Spacer(Modifier.height(16.dp))
        Text(expenseType.getLocalisedString(), color = textColor, fontWeight = fontWeight)
    }
}
