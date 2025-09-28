package com.lightfeather.masarify.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payment
import androidx.compose.ui.graphics.vector.ImageVector
import com.lightfeather.designsystem.model.UiBottomNavigationItem
import com.lightfeather.masarify.MR
import com.lightfeather.masarify.navigation.Route
import com.lightfeather.masarify.navigation.routes.AccountsRoute
import com.lightfeather.masarify.navigation.routes.DashboardRoute
import com.lightfeather.masarify.navigation.routes.MoreRoute
import com.lightfeather.masarify.navigation.routes.TransactionsRoute
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

    data object Transactions : AppTopLevelRoutes(TransactionsRoute) {
        override val icon: ImageVector
            get() = Icons.Default.Payment
        override val label: StringResource
            get() = MR.strings.transactions
    }

    data object Accounts : AppTopLevelRoutes(AccountsRoute) {
        override val icon: ImageVector
            get() = Icons.Default.AccountBalance
        override val label: StringResource
            get() = MR.strings.accounts
    }

    data object More : AppTopLevelRoutes(MoreRoute) {
        override val icon: ImageVector
            get() = Icons.Default.MoreHoriz
        override val label: StringResource
            get() = MR.strings.more
    }
}
