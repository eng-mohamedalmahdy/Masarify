package data.datasource

import domain.Category

interface CategoryDatasource {

    fun createCategory(category: Category): Int

    fun updateCategory(category: Category)

    fun deleteCategory(category: Category): Boolean

    fun getAllCategories(): List<Category>

    fun getCategoryById(id: Int): Category

}