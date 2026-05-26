package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeBankNameRepository
import tech.lightfeather.domain.model.BankName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private val testBankName = BankName(id = -1, name = "Test Bank")

class BankNameUseCaseTest {
    @Test
    fun createBankNameSuccessStoresAndReturnsId() =
        runTest {
            val repo = FakeBankNameRepository()
            val result = BankNameUseCase.CreateBankName(repo)(testBankName)
            assertTrue(result.isSuccess)
            val id = result.getOrNull()!!
            assertTrue(id > 0)
            assertEquals("Test Bank", repo.store[id]?.name)
        }

    @Test
    fun createBankNameFailureReturnsDomainResultFailure() =
        runTest {
            val repo = FakeBankNameRepository(shouldFail = true)
            val result = BankNameUseCase.CreateBankName(repo)(testBankName)
            assertTrue(result.isFailure)
        }

    @Test
    fun updateBankNameSuccessUpdatesStore() =
        runTest {
            val repo = FakeBankNameRepository()
            val id = BankNameUseCase.CreateBankName(repo)(testBankName).getOrNull()!!
            val updated = testBankName.copy(id = id, name = "Updated Bank")
            val result = BankNameUseCase.UpdateBankName(repo)(updated)
            assertTrue(result.isSuccess)
            assertEquals("Updated Bank", repo.store[id]?.name)
        }

    @Test
    fun deleteBankNameSuccessRemovesFromStore() =
        runTest {
            val repo = FakeBankNameRepository()
            val id = BankNameUseCase.CreateBankName(repo)(testBankName).getOrNull()!!
            val stored = repo.store[id]!!
            val result = BankNameUseCase.DeleteBankName(repo)(stored)
            assertTrue(result.isSuccess)
            assertNull(repo.store[id])
        }

    @Test
    fun getAllBankNamesFlowReflectsCreatedBankName() =
        runTest {
            val repo = FakeBankNameRepository()
            BankNameUseCase.CreateBankName(repo)(testBankName)
            val result = BankNameUseCase.GetAllBankNames(repo)()
            assertTrue(result.isSuccess)
            val list = result.getOrNull()!!.first()
            assertEquals(1, list.size)
            assertEquals("Test Bank", list.first().name)
        }

    @Test
    fun getBankNameByIdFailsWhenNotFound() =
        runTest {
            val repo = FakeBankNameRepository()
            val result = BankNameUseCase.GetBankNameById(repo)(999)
            assertTrue(result.isFailure)
        }
}
