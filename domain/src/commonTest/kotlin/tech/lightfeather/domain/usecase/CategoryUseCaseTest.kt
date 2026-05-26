package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeCategoryRepository
import tech.lightfeather.domain.fake.noSyncHelper
import tech.lightfeather.domain.model.Category
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val testCategory = Category(id = -1, name = "Food", description = null, color = "#FF0000", icon = "food")

class CategoryUseCaseTest {
    @Test
    fun createCategorySuccessStoresAndReturnsId() =
        runTest {
            val repo = FakeCategoryRepository()

            val result = CreateCategory(repo, noSyncHelper())(testCategory)

            assertTrue(result.isSuccess)
            val id = result.getOrNull()!!
            assertEquals("Food", repo.store[id]?.name)
        }

    @Test
    fun createCategoryFailurePropagatessError() =
        runTest {
            val repo = FakeCategoryRepository(shouldFail = true)

            val result = CreateCategory(repo, noSyncHelper())(testCategory)

            assertTrue(result.isFailure)
            assertTrue(repo.store.isEmpty())
        }

    @Test
    fun updateCategorySuccessUpdatesStoredCategory() =
        runTest {
            val repo = FakeCategoryRepository()
            val id = CreateCategory(repo, noSyncHelper())(testCategory).getOrNull()!!
            val updated = testCategory.copy(id = id, name = "Groceries")

            val result = UpdateCategory(repo, noSyncHelper())(updated)

            assertTrue(result.isSuccess)
            assertEquals("Groceries", repo.store[id]?.name)
        }

    @Test
    fun deleteCategorySuccessRemovesFromStore() =
        runTest {
            val repo = FakeCategoryRepository()
            val id = CreateCategory(repo, noSyncHelper())(testCategory).getOrNull()!!
            val stored = repo.store[id]!!

            val result = DeleteCategory(repo, noSyncHelper())(stored)

            assertTrue(result.isSuccess)
            assertTrue(repo.store.isEmpty())
        }

    @Test
    fun getAllCategoriesFlowReflectsCreatedCategory() =
        runTest {
            val repo = FakeCategoryRepository()
            CreateCategory(repo, noSyncHelper())(testCategory)

            val result = GetAllCategories(repo)()

            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertEquals(1, list.size)
            assertEquals("Food", list.first().name)
        }

    @Test
    fun getCategoryByIdReturnsStoredCategory() =
        runTest {
            val repo = FakeCategoryRepository()
            val id = CreateCategory(repo, noSyncHelper())(testCategory).getOrNull()!!

            val result = GetCategoryById(repo)(id)

            assertTrue(result.isSuccess)
            assertEquals("Food", result.getOrNull()?.name)
        }
}
