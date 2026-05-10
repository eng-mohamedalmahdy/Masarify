package tech.lightfeather.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.lightfeather.domain.model.Category
import tech.lightfeather.domain.model.DomainResult

interface CategoryRepository {
    suspend fun createCategory(category: Category): DomainResult<Int>

    suspend fun updateCategory(category: Category): DomainResult<Boolean>

    suspend fun deleteCategory(category: Category): DomainResult<Boolean>

    fun getAllCategories(): DomainResult<Flow<List<Category>>>

    suspend fun getCategoryById(id: Int): DomainResult<Category>

    fun getAllCategoryIcons(): DomainResult<Flow<List<Any>>>

    fun getExpenseCategoriesByUsage(limit: Int): DomainResult<Flow<List<Category>>>

    suspend fun updateRemoteId(
        localId: Int,
        remoteId: Long,
    ): DomainResult<Unit>

    suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?>

    suspend fun getRemoteIdByLocalId(localId: Int): DomainResult<Long?>

    suspend fun getUnsyncedIds(): DomainResult<List<Int>>

    suspend fun getLocalIdByResourceKey(resourceKey: String): DomainResult<Int?>
}
