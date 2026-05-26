package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.GetAllCategories
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.categories.CategoriesPageViewModel

internal fun buildCategoriesViewModel(
    navigator: Navigator = CapturingNavigator(),
    categoryRepository: FakeCategoryRepository = FakeCategoryRepository(),
): CategoriesPageViewModel =
    CategoriesPageViewModel(
        navigator = navigator,
        getAllCategories = GetAllCategories(categoryRepository),
    )
