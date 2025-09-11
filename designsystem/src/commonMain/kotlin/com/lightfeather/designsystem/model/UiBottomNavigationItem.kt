package com.lightfeather.designsystem.model

import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.resources.StringResource

interface UiBottomNavigationItem {
    val icon: ImageVector
    val label: StringResource
}
