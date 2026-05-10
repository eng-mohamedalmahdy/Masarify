package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import tech.lightfeather.domain.model.AttachmentEntityType
import tech.lightfeather.domain.model.Category
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.AttachmentRepository
import tech.lightfeather.domain.repository.CategoryRepository

class CreateCategory(
    private val categoryRepository: CategoryRepository,
    private val syncHelper: SyncEnqueueHelper,
) {
    suspend operator fun invoke(category: Category) =
        categoryRepository.createCategory(category).also { result ->
            result.getOrNull()?.let { newId ->
                syncHelper.enqueue("CATEGORY", "CREATE", category.copy(id = newId), Category.serializer(), newId)
            }
        }
}

class UpdateCategory(
    private val categoryRepository: CategoryRepository,
    private val syncHelper: SyncEnqueueHelper,
) {
    suspend operator fun invoke(category: Category) =
        categoryRepository.updateCategory(category).also { result ->
            if (result.isSuccess) {
                syncHelper.enqueue("CATEGORY", "UPDATE", category, Category.serializer(), category.id)
            }
        }
}

class DeleteCategory(
    private val categoryRepository: CategoryRepository,
    private val syncHelper: SyncEnqueueHelper,
) {
    suspend operator fun invoke(category: Category) =
        categoryRepository.deleteCategory(category).also { result ->
            if (result.isSuccess) {
                syncHelper.enqueue("CATEGORY", "DELETE", category, Category.serializer(), category.id)
            }
        }
}

class GetCategoryById(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(id: Int) = categoryRepository.getCategoryById(id)
}

class GetAllCategories(
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke() = categoryRepository.getAllCategories()
}

class GetExpenseCategoriesByUsage(
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(limit: Int): DomainResult<Flow<List<Category>>> =
        categoryRepository.getExpenseCategoriesByUsage(limit)
}

class GetAllCategoryIcons(
    private val repository: CategoryRepository,
    private val attachmentRepository: AttachmentRepository,
) {
    suspend operator fun invoke(): DomainResult<Flow<List<Any>>> {
        val apiIcons = repository.getAllCategoryIcons()
        val savedIcons = attachmentRepository.getAttachmentsOfEntityType(AttachmentEntityType.CATEGORY)
        return apiIcons.combine(savedIcons) { api, saved ->
            api.combine(saved) { apiIcons, savedIcons ->
                savedIcons.map { it.fileContent }.plus(apiIcons)
            }
        }
    }
}
