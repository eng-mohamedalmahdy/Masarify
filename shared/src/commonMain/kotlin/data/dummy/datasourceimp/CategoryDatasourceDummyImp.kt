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
        return  DummyDomainModelsProviders.category
    }
}