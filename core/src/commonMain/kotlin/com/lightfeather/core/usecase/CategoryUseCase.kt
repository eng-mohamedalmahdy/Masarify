package usecase

import data.repository.CategoryRepository
import domain.Category

class CreateCategory(private val categoryRepository: CategoryRepository) {
    operator fun invoke(category: Category) = categoryRepository.createCategory(category)
}

class UpdateCategory(private val categoryRepository: CategoryRepository) {
    operator fun invoke(category: Category) = categoryRepository.updateCategory(category)
}

class DeleteCategory(private val categoryRepository: CategoryRepository) {
    operator fun invoke(category: Category) = categoryRepository.deleteCategory(category)
}

class GetCategoryById(private val categoryRepository: CategoryRepository) {
    operator fun invoke(id: Int) = categoryRepository.getCategoryById(id)
}

class GetAllCategories(private val categoryRepository: CategoryRepository) {
    operator fun invoke() = categoryRepository.getAllCategories()
}

