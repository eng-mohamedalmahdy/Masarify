package tech.lightfeather.domain.usecase

import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeAccountRepository
import tech.lightfeather.domain.fake.FakeCategoryRepository
import tech.lightfeather.domain.fake.FakeTransactionRepository
import tech.lightfeather.domain.fake.noSyncHelper
import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.Category
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.model.transaction.Transaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val txCurrency = Currency(name = "USD", sign = "$", id = 1)
private val txAccount =
    Account(
        name = "Main",
        currency = txCurrency,
        description = null,
        balance = 2_000.0,
        color = "#AABBCC",
        logo = null,
        id = 1,
    )
private val txCategory = Category(id = 1, name = "Salary", description = null, color = "#00FF00", icon = "salary")
private val expCat = Category(id = 2, name = "Food", description = null, color = "#FF0000", icon = "food")

class TransactionUseCaseTest {
    private fun income() =
        Transaction.Income(
            incomeId = -1,
            incomeName = "Salary",
            incomeAmount = 5_000.0,
            incomeTimestamp = 0L,
            incomeAccount = txAccount,
            source = txCategory,
        )

    private fun expense() =
        Transaction.Expense(
            expenseId = -1,
            expenseName = "Groceries",
            expenseAmount = 150.0,
            expenseTimestamp = 0L,
            expenseAccount = txAccount,
            categories = listOf(expCat),
        )

    @Test
    fun createTransactionIncomeSuccessStoresAndReturnsId() =
        runTest {
            val txRepo = FakeTransactionRepository()
            val useCase = CreateTransaction(txRepo, noSyncHelper(), FakeAccountRepository(), FakeCategoryRepository())

            val result = useCase(income())

            assertTrue(result.isSuccess)
            val id = result.getOrNull()!!
            assertEquals("Salary", txRepo.store[id]?.name)
        }

    @Test
    fun createTransactionExpenseSuccessStoresAndReturnsId() =
        runTest {
            val txRepo = FakeTransactionRepository()
            val useCase = CreateTransaction(txRepo, noSyncHelper(), FakeAccountRepository(), FakeCategoryRepository())

            val result = useCase(expense())

            assertTrue(result.isSuccess)
            val id = result.getOrNull()!!
            assertEquals("Groceries", txRepo.store[id]?.name)
        }

    @Test
    fun createTransactionFailurePropagatessError() =
        runTest {
            val txRepo = FakeTransactionRepository(shouldFail = true)
            val useCase = CreateTransaction(txRepo, noSyncHelper(), FakeAccountRepository(), FakeCategoryRepository())

            val result = useCase(income())

            assertTrue(result.isFailure)
            assertTrue(txRepo.store.isEmpty())
        }

    @Test
    fun updateTransactionSuccessUpdatesStoredTransaction() =
        runTest {
            val txRepo = FakeTransactionRepository()
            val createUseCase =
                CreateTransaction(txRepo, noSyncHelper(), FakeAccountRepository(), FakeCategoryRepository())
            val newId = createUseCase(income()).getOrNull()!!
            val updated = income().copy(incomeId = newId, incomeName = "Bonus")

            val updateUseCase =
                UpdateTransaction(txRepo, noSyncHelper(), FakeAccountRepository(), FakeCategoryRepository())
            val result = updateUseCase(updated)

            assertTrue(result.isSuccess)
            assertEquals("Bonus", txRepo.store[newId]?.name)
        }

    @Test
    fun deleteTransactionSuccessRemovesFromStore() =
        runTest {
            val txRepo = FakeTransactionRepository()
            val createUseCase =
                CreateTransaction(txRepo, noSyncHelper(), FakeAccountRepository(), FakeCategoryRepository())
            val newId = createUseCase(income()).getOrNull()!!

            val deleteUseCase =
                DeleteTransaction(txRepo, noSyncHelper(), FakeAccountRepository(), FakeCategoryRepository())
            val result = deleteUseCase(newId.toLong())

            assertTrue(result.isSuccess)
            assertTrue(txRepo.store.isEmpty())
        }

    @Test
    fun getTransactionCountReflectsNumberOfCreatedTransactions() =
        runTest {
            val txRepo = FakeTransactionRepository()
            val createUseCase =
                CreateTransaction(txRepo, noSyncHelper(), FakeAccountRepository(), FakeCategoryRepository())
            createUseCase(income())
            createUseCase(expense())

            val result = GetTransactionCount(txRepo)()

            assertTrue(result.isSuccess)
            assertEquals(2L, result.getOrNull())
        }
}
