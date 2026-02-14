package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.AttachmentEntityType
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.repository.AttachmentRepository
import com.lightfeather.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class CreateCategory(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(category: Category) = categoryRepository.createCategory(category)
}

class UpdateCategory(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(category: Category) = categoryRepository.updateCategory(category)
}

class DeleteCategory(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(category: Category) = categoryRepository.deleteCategory(category)
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
