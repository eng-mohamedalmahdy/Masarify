package tech.lightfeather.masarify.test

import tech.lightfeather.domain.model.Category
import tech.lightfeather.domain.usecase.DeleteCategory
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.deletecategory.DeleteCategoryPageViewModel

internal val testCategory =
    Category(
        id = 1,
        name = "Food",
        description = "Food expenses",
        color = "#FF5722",
        icon = "https://example.com/food.png",
    )

internal fun buildDeleteCategoryViewModel(
    navigator: Navigator = CapturingNavigator(),
    category: Category = testCategory,
): DeleteCategoryPageViewModel {
    val categoryRepo = FakeCategoryRepository()
    return DeleteCategoryPageViewModel(
        navigator = navigator,
        deleteCategory =
            DeleteCategory(
                categoryRepository = categoryRepo,
                syncHelper = buildSyncEnqueueHelper(),
            ),
        toBeDeletedCategory = category,
    )
}
