package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.data.remote.getImagesIcons
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.error.AppError
import com.lightfeather.domain.repository.CategoryRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.V_categories

// DomainResult pattern requires catching all exceptions for proper error handling
@Suppress("TooGenericExceptionCaught")
class
CategoryRepositoryImpl(
    private val database: SharedDatabase,
    private val httpClient: HttpClient,
) : CategoryRepository {
    override suspend fun createCategory(category: Category): DomainResult<Int> =
        try {
            val result =
                database {
                    val categoriesQueries = it.categoriesQueries
                    categoriesQueries.transactionWithResult {
                        categoriesQueries.insertCategory(
                            name = category.name,
                            description = category.description ?: "",
                            color = category.color,
                            icon = category.icon,
                            is_default = if (category.isDefault) 1L else 0L,
                            resource_key = category.resourceKey,
                        )
                        categoriesQueries.selectLastInsertedRowId().awaitAsOne()
                    }
                }
            DomainResult.Success(result.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error creating category"))
        }

    override suspend fun updateCategory(category: Category): DomainResult<Boolean> {
        return try {
            // Prevent editing default categories
            if (category.isDefault) {
                return DomainResult.Failure(
                    AppError.InternalError("Cannot edit default categories"),
                )
            }

            val result =
                database {
                    it.categoriesQueries.updateById(
                        name = category.name,
                        description = category.description ?: "",
                        color = category.color,
                        icon = category.icon,
                        is_default = if (category.isDefault) 1L else 0L,
                        resource_key = category.resourceKey,
                        id = category.id.toLong(),
                    )
                }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating category"))
        }
    }

    override suspend fun deleteCategory(category: Category): DomainResult<Boolean> {
        return try {
            // Prevent deleting default categories
            if (category.isDefault) {
                return DomainResult.Failure(
                    AppError.InternalError("Cannot delete default categories"),
                )
            }

            val result = database { it.categoriesQueries.deleteById(category.id.toLong()) }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error deleting category"))
        }
    }

    override fun getAllCategories(): DomainResult<Flow<List<Category>>> =
        try {
            val categoriesFlow: Flow<List<Category>> =
                flow {
                    val flow =
                        database { db ->
                            db.categoriesQueries
                                .selectAllCategories()
                                .asFlow()
                                .map { query -> query.awaitAsList().map { it.toDomain() } }
                        }
                    emitAll(flow)
                }
            DomainResult.Success(categoriesFlow)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting categories"))
        }

    override suspend fun getCategoryById(id: Int): DomainResult<Category> =
        try {
            val category =
                database {
                    it.categoriesQueries.selectCategoryById(id.toLong()).awaitAsOne()
                }
            DomainResult.Success(category.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting category"))
        }

    override fun getExpenseCategoriesByUsage(limit: Int): DomainResult<Flow<List<Category>>> =
        try {
            val categoriesFlow: Flow<List<Category>> =
                flow {
                    val flow =
                        database { db ->
                            db.transactionsQueries
                                .getExpenseCategoriesByUsage(limit.toLong())
                                .asFlow()
                                .map { query ->
                                    query.awaitAsList().map { row ->
                                        Category(
                                            id = row.category_id.toInt(),
                                            name = row.category_name,
                                            description = row.category_description,
                                            color = row.category_color,
                                            icon = row.category_icon,
                                            isDefault = row.is_default == 1L,
                                            resourceKey = row.resource_key,
                                        )
                                    }
                                }
                        }
                    emitAll(flow)
                }
            DomainResult.Success(categoriesFlow)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting categories by usage"))
        }

    override fun getAllCategoryIcons(): DomainResult<Flow<List<Any>>> =
        try {
            val iconsFlow: Flow<List<Any>> =
                flow {
                    val response = getImagesIcons(httpClient)
                    emit(response.data)
                }
            DomainResult.Success(iconsFlow)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error fetching category icons"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun updateRemoteId(localId: Int, remoteId: Long): DomainResult<Unit> =
        try {
            database { it.categoriesQueries.updateRemoteId(remoteId = remoteId, id = localId.toLong()) }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating remote id"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getLocalIdByRemoteId(remoteId: Long): DomainResult<Int?> =
        try {
            val id = database { it.categoriesQueries.getLocalIdByRemoteId(remoteId).awaitAsOneOrNull() }
            DomainResult.Success(id?.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting local id"))
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> =
        try {
            val ids = database { it.categoriesQueries.getUnsyncedCategoryIds().awaitAsList() }
            DomainResult.Success(ids.map { it.toInt() })
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting unsynced ids"))
        }

    private fun lightfeather.masarify.database.Categories.toDomain(): Category =
        Category(
            id = id.toInt(),
            name = name,
            description = description,
            color = color,
            icon = icon,
            isDefault = is_default == 1L,
            resourceKey = resource_key,
        )

    private fun V_categories.toDomain(): Category =
        Category(
            id = categoryId.toInt(),
            name = categoryName,
            description = categoryDescription,
            color = categoryColor,
            icon = categoryIcon,
            isDefault = categoryIsDefault == 1L,
            resourceKey = categoryResourceKey,
        )
}
