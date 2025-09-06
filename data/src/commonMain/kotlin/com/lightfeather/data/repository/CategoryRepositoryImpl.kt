package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.domain.repository.CategoryRepository
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.error.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import lightfeather.masarify.database.V_categories

class
CategoryRepositoryImpl(
    private val database: SharedDatabase
) : CategoryRepository {

    override suspend fun createCategory(category: Category): DomainResult<Int> {
        return try {
            val result = database {
                val categoriesQueries = it.categoriesQueries
                categoriesQueries.transactionWithResult {
                    categoriesQueries.insertCategory(
                        name = category.name,
                        description = category.description ?: "",
                        color = category.color,
                        icon = category.icon
                    )
                    categoriesQueries.selectLastInsertedRowId().awaitAsOne()
                }
            }
            DomainResult.Success(result.toInt())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error creating category"))
        }
    }

    override suspend fun updateCategory(category: Category): DomainResult<Boolean> {
        return try {
            val result = database {
                it.categoriesQueries.updateById(
                    name = category.name,
                    description = category.description ?: "",
                    color = category.color,
                    icon = category.icon,
                    id = category.id.toLong()
                )
            }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error updating category"))
        }
    }

    override suspend fun deleteCategory(category: Category): DomainResult<Boolean> {
        return try {
            val result = database { it.categoriesQueries.deleteById(category.id.toLong()) }
            DomainResult.Success(result > 0)
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error deleting category"))
        }
    }

    override suspend fun getAllCategories(): DomainResult<Flow<List<Category>>> {
        return try {
            val categoriesFlow: Flow<List<Category>> = flow {
                val flow = database { db ->
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
    }

    override suspend fun getCategoryById(id: Int): DomainResult<Category> {
        return try {
            val category = database {
                it.categoriesQueries.selectCategoryById(id.toLong()).awaitAsOne()
            }
            DomainResult.Success(category.toDomain())
        } catch (e: Exception) {
            DomainResult.Failure(AppError.InternalError(e.message ?: "Error getting category"))
        }
    }

    override fun getAllCategoryIcons(): DomainResult<Flow<List<Any>>> {
        // This method might require external resources or a predefined list of icons
        // For now, returning an empty list as this would typically be implemented differently
        return DomainResult.Success(flow { emit(emptyList<Any>()) })
    }

    private fun lightfeather.masarify.database.Categories.toDomain(): Category {
        return Category(
            id = id.toInt(),
            name = name,
            description = description,
            color = color,
            icon = icon
        )
    }

    private fun V_categories.toDomain(): Category {
        return Category(
            id = categoryId.toInt(),
            name = categoryName,
            description = categoryDescription,
            color = categoryColor,
            icon = categoryIcon
        )
    }
}