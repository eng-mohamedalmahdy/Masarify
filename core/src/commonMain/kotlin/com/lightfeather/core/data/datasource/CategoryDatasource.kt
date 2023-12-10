package com.lightfeather.core.data.datasource

import com.lightfeather.core.domain.Category
import kotlinx.coroutines.flow.Flow

interface CategoryDatasource {

    suspend fun createCategory(category: Category): Int

    suspend fun updateCategory(category: Category)

    suspend fun deleteCategory(category: Category): Boolean

    suspend fun getAllCategories(): Flow<List<Category>>

    suspend fun getCategoryById(id: Int): Category

    suspend fun getAllCategoryIcons(): List<String>

}