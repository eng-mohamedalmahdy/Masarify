@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.lightfeather.masarify.page.categories

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import com.lightfeather.domain.model.Category

internal sealed interface CategoriesPageIntent {
    data object LoadData : CategoriesPageIntent

    sealed class NavigationIntent(
        open val category: Category?,
        open val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
    ) : CategoriesPageIntent {
        data class AddCategory(
            override val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
        ) : NavigationIntent(
                navigator = navigator,
                category = null,
            )

        data class UpdateCategory(
            val selectedCategory: Category,
            override val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
        ) : NavigationIntent(
                category = selectedCategory,
                navigator = navigator,
            )
    }

    data class DeleteCategory(
        val category: Category,
    ) : CategoriesPageIntent

    data class ClearNavigation(
        val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
    ) : CategoriesPageIntent
}
