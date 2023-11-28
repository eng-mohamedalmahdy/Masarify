package data.local

import com.lightfeather.core.data.datasource.CategoryDatasource
import com.lightfeather.core.domain.Category
import com.lightfeather.masarify.database.CategoriesQueries
import data.remote.getImagesIcons
import io.ktor.client.HttpClient

class CategoryDatasourceReleaseImp(
    private val queries: CategoriesQueries,
    private val httpClient: HttpClient,
) : CategoryDatasource {
    override suspend fun createCategory(category: Category): Int {
        with(category) {
            queries.insertCategory(name, description ?: "", color, icon)
        }
        return queries.selectLastInsertedRowId().executeAsOne().toInt()
    }

    override suspend fun updateCategory(category: Category) {
        with(category) {
            queries.updateById(name, description ?: "", color, icon, id.toLong())
        }
    }

    override suspend fun deleteCategory(category: Category): Boolean {
        queries.deleteById(category.id.toLong())
        return true
    }

    override suspend fun getAllCategories(): List<Category> {

        return queries.selectAllCategories { id, name, desceription, color, icon ->
            Category(
                id.toInt(), name, desceription,
                color, icon
            )
        }.executeAsList()
    }

    override suspend fun getCategoryById(id: Int): Category {
        return queries.selectCategoryById(id.toLong()){_, name, desceription, color, icon ->
            Category(id,name,desceription,color,icon)
        }.executeAsOne()
    }

    override suspend fun getAllCategoryIcons(): List<String> {
        return getImagesIcons(httpClient).data
    }
}