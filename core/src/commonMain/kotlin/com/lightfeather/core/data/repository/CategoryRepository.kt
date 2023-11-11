package com.lightfeather.core.data.repository

import com.lightfeather.core.data.datasource.CategoryDatasource
import com.lightfeather.core.domain.Category

class CategoryRepository(private val datasource: CategoryDatasource) {

    suspend fun createCategory(category: Category): Int = datasource.createCategory(category)

    suspend fun updateCategory(category: Category) = datasource.updateCategory(category)

    suspend fun deleteCategory(category: Category): Boolean = datasource.deleteCategory(category)

    suspend fun getAllCategories(): List<Category> = datasource.getAllCategories()

    suspend fun getCategoryById(id: Int): Category = datasource.getCategoryById(id)
    suspend fun getAllCategoryIcons() = datasource.getAllCategoryIcons()

}