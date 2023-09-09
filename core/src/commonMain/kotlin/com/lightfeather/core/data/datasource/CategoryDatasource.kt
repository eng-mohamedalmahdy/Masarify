package com.lightfeather.core.data.datasource

import com.lightfeather.core.domain.Category

interface CategoryDatasource {

    suspend  fun createCategory(category: Category): Int

    suspend  fun updateCategory(category: Category)

    suspend  fun deleteCategory(category: Category): Boolean

    suspend  fun getAllCategories(): List<Category>

    suspend  fun getCategoryById(id: Int): Category

}