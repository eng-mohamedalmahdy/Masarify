package com.lightfeather.masarify.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.ui.graphics.vector.ImageVector
import com.lightfeather.designsystem.model.UiBottomNavigationItem
import com.lightfeather.masarify.MR
import com.lightfeather.masarify.navigation.Route
import com.lightfeather.masarify.navigation.routes.DashboardRoute
import dev.icerock.moko.resources.StringResource

sealed class AppTopLevelRoutes(
    val route: Route,
) : UiBottomNavigationItem {
    data object Dashboard : AppTopLevelRoutes(DashboardRoute) {
        override val icon: ImageVector
            get() = Icons.Default.Dashboard
        override val label: StringResource
            get() = MR.strings.dashboard
    }
}
