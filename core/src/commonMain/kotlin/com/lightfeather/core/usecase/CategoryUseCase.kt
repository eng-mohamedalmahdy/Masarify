package com.lightfeather.core.usecase

import com.lightfeather.core.data.repository.CategoryRepository
import com.lightfeather.core.domain.Category

class CreateCategory(private val categoryRepository: CategoryRepository) {
     suspend operator fun invoke(category: Category) = categoryRepository.createCategory(category)
}

class UpdateCategory(private val categoryRepository: CategoryRepository) {
     suspend operator fun invoke(category: Category) = categoryRepository.updateCategory(category)
}

class DeleteCategory(private val categoryRepository: CategoryRepository) {
     suspend operator fun invoke(category: Category) = categoryRepository.deleteCategory(category)
}

class GetCategoryById(private val categoryRepository: CategoryRepository) {
     suspend operator fun invoke(id: Int) = categoryRepository.getCategoryById(id)
}

class GetAllCategories(private val categoryRepository: CategoryRepository) {
     suspend operator fun invoke() = categoryRepository.getAllCategories()
}

