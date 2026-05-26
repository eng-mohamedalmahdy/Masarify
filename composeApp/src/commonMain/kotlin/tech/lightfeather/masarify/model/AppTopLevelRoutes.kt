package tech.lightfeather.masarify.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payment
import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.resources.StringResource
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.model.UiBottomNavigationItem
import tech.lightfeather.masarify.navigation.Route
import tech.lightfeather.masarify.navigation.routes.AccountsRoute
import tech.lightfeather.masarify.navigation.routes.DashboardRoute
import tech.lightfeather.masarify.navigation.routes.MoreRoute
import tech.lightfeather.masarify.navigation.routes.TransactionsRoute

sealed class AppTopLevelRoutes(
    val route: Route,
) : UiBottomNavigationItem {
    data object Dashboard : AppTopLevelRoutes(DashboardRoute) {
        override val icon: ImageVector
            get() = Icons.Default.Dashboard
        override val label: StringResource
            get() = MR.strings.dashboard
    }

    data object Transactions : AppTopLevelRoutes(TransactionsRoute()) {
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
