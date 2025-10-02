package com.lightfeather.domain.repository

import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.DomainResult
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun createCategory(category: Category): DomainResult<Int>

    suspend fun updateCategory(category: Category): DomainResult<Boolean>

    suspend fun deleteCategory(category: Category): DomainResult<Boolean>

     fun getAllCategories(): DomainResult<Flow<List<Category>>>

    suspend fun getCategoryById(id: Int): DomainResult<Category>

    fun getAllCategoryIcons(): DomainResult<Flow<List<Any>>>
}
