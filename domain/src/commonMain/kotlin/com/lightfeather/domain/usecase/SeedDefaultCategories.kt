package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.DefaultCategory
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.first

/**
 * Use case to seed default categories on first app launch
 * Checks if categories already exist to avoid duplicate seeding
 */
class SeedDefaultCategories(
    private val categoryRepository: CategoryRepository,
) {
    /**
     * Seed default categories if they don't already exist
     * Returns true if seeding was successful or categories already exist
     * DomainResult pattern requires catching all exceptions for proper error handling
     */
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(): DomainResult<Boolean> {
        return try {
            // Check if categories already exist
            val existingCategories =
                categoryRepository.getAllCategories().foldResult(
                    onSuccess = { categoriesFlow ->
                        categoriesFlow.first()
                    },
                    onFailure = { error ->
                        emptyList<Category>()
                    },
                )

            // If categories exist, skip seeding
            if (existingCategories.isNotEmpty()) {
                return DomainResult.Success(true)
            }

            var successCount = 0

            DefaultCategory.getAllCategories().forEach { category ->
                categoryRepository.createCategory(category).fold(
                    onSuccess = { id ->
                        successCount++
                    },
                    onFailure = { error ->
                    },
                )
            }

            val allSuccess = successCount == DefaultCategory.entries.size

            DomainResult.Success(allSuccess)
        } catch (e: Exception) {
            DomainResult.Failure(
                com.lightfeather.domain.model.error.AppError.InternalError(
                    "Failed to seed categories: ${e.message}",
                ),
            )
        }
    }
}
