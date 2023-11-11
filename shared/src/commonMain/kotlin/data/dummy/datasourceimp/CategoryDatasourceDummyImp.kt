package data.dummy.datasourceimp

import com.lightfeather.core.data.datasource.CategoryDatasource
import com.lightfeather.core.domain.Category
import data.dummy.model.DummyDomainModelsProviders

object CategoryDatasourceDummyImp : CategoryDatasource {
    override suspend fun createCategory(category: Category): Int {
        return 5
    }

    override suspend fun updateCategory(category: Category) {

    }

    override suspend fun deleteCategory(category: Category): Boolean {
        return true
    }

    override suspend fun getAllCategories(): List<Category> {
        return List(18) { DummyDomainModelsProviders.category }
    }

    override suspend fun getCategoryById(id: Int): Category {
        return DummyDomainModelsProviders.category
    }

    override suspend fun getAllCategoryIcons(): List<String> {
        return listOf(
            "https://img.icons8.com/ios/50/cute-pumpkin.png",
            "https://img.icons8.com/ios/50/cat--v1.png",
            "https://img.icons8.com/small/512/jake.png",
            "https://img.icons8.com/ios/50/cute-pumpkin.png",
            "https://img.icons8.com/ios/50/cat--v1.png",
            "https://img.icons8.com/small/512/jake.png",

            "https://img.icons8.com/ios/50/cute-pumpkin.png",
            "https://img.icons8.com/ios/50/cat--v1.png",
            "https://img.icons8.com/small/512/jake.png",
            "https://img.icons8.com/ios/50/cute-pumpkin.png",
            "https://img.icons8.com/ios/50/cat--v1.png",
            "https://img.icons8.com/small/512/jake.png",

            "https://img.icons8.com/ios/50/cute-pumpkin.png",
            "https://img.icons8.com/ios/50/cat--v1.png",
            "https://img.icons8.com/small/512/jake.png",
            "https://img.icons8.com/ios/50/cute-pumpkin.png",
            "https://img.icons8.com/ios/50/cat--v1.png",
            "https://img.icons8.com/small/512/jake.png",
        )
    }
}