package data.repository

import data.datasource.CategoryDatasource
import domain.Category

class CategoryRepository (private val datasource: CategoryDatasource){

    fun createCategory(category: Category): Int = datasource.createCategory(category)

    fun updateCategory(category: Category) = datasource.updateCategory(category)

    fun deleteCategory(category: Category): Boolean = datasource.deleteCategory(category)

    fun getAllCategories(): List<Category> = datasource.getAllCategories()

    fun getCategoryById(id: Int): Category = datasource.getCategoryById(id)

}