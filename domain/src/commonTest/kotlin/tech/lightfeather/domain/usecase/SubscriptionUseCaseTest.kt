package tech.lightfeather.domain.usecase

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import tech.lightfeather.domain.fake.FakeRevenueCatSdk
import tech.lightfeather.domain.fake.FakeSubscriptionRepository
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.SubscriptionPlan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SubscriptionUseCaseTest {
    @Test
    fun getSubscriptionStatusReturnsProWhenActive() =
        runTest {
            val repo = FakeSubscriptionRepository(isProActive = true)

            val result = GetSubscriptionStatusUseCase(repo)().toList().last()

            assertIs<DomainResult.Success<*>>(result)
            assertTrue((result as DomainResult.Success).value.isProActive)
            assertEquals(SubscriptionPlan.PRO, result.value.plan)
        }

    @Test
    fun getSubscriptionStatusReturnsFreeByDefault() =
        runTest {
            val repo = FakeSubscriptionRepository(isProActive = false)

            val result = GetSubscriptionStatusUseCase(repo)().toList().last()

            assertIs<DomainResult.Success<*>>(result)
            assertEquals(SubscriptionPlan.FREE, (result as DomainResult.Success).value.plan)
        }

    @Test
    fun getSubscriptionStatusReturnsFailureOnError() =
        runTest {
            val repo = FakeSubscriptionRepository(shouldFail = true)

            val result = GetSubscriptionStatusUseCase(repo)().toList().last()

            assertIs<DomainResult.Failure<*>>(result)
        }

    @Test
    fun isProActiveTrueWhenProSubscription() =
        runTest {
            val repo = FakeSubscriptionRepository(isProActive = true)

            val result = IsProActiveUseCase(repo)().toList().last()

            assertTrue(result)
        }

    @Test
    fun isProActiveFalseForFreeUser() =
        runTest {
            val repo = FakeSubscriptionRepository(isProActive = false)

            val result = IsProActiveUseCase(repo)().toList().last()

            assertFalse(result)
        }

    @Test
    fun isProActiveFalseOnRepositoryFailure() =
        runTest {
            val repo = FakeSubscriptionRepository(shouldFail = true)

            val result = IsProActiveUseCase(repo)().toList().last()

            assertFalse(result)
        }

    @Test
    fun purchaseProLinksCustomerOnSuccess() =
        runTest {
            val repo = FakeSubscriptionRepository()
            val sdk = FakeRevenueCatSdk(shouldSucceed = true)

            PurchaseProUseCase(sdk, repo)()

            assertEquals(1, repo.linkCallCount)
        }

    @Test
    fun purchaseProReturnsFailureOnSdkError() =
        runTest {
            val repo = FakeSubscriptionRepository()
            val sdk = FakeRevenueCatSdk(shouldSucceed = false)

            val result = PurchaseProUseCase(sdk, repo)()

            assertIs<DomainResult.Failure<*>>(result)
            assertEquals(0, repo.linkCallCount)
        }

    @Test
    fun purchaseProCancelledDoesNotLinkCustomer() =
        runTest {
            val repo = FakeSubscriptionRepository()
            val sdk = FakeRevenueCatSdk(shouldSucceed = false, shouldCancel = true)

            val result = PurchaseProUseCase(sdk, repo)()

            assertIs<DomainResult.Failure<*>>(result)
            assertEquals(0, repo.linkCallCount)
        }

    @Test
    fun restorePurchasesLinksCustomerOnSuccess() =
        runTest {
            val repo = FakeSubscriptionRepository()
            val sdk = FakeRevenueCatSdk(shouldSucceed = true)

            RestorePurchasesUseCase(sdk, repo)()

            assertEquals(1, repo.linkCallCount)
        }

    @Test
    fun restorePurchasesReturnsFailureOnSdkError() =
        runTest {
            val repo = FakeSubscriptionRepository()
            val sdk = FakeRevenueCatSdk(shouldSucceed = false)

            val result = RestorePurchasesUseCase(sdk, repo)()

            assertIs<DomainResult.Failure<*>>(result)
            assertEquals(0, repo.linkCallCount)
        }

    @Test
    fun linkRevenueCatCustomerDelegatesToRepository() =
        runTest {
            val repo = FakeSubscriptionRepository()

            LinkRevenueCatCustomerUseCase(repo)("rc_123")

            assertEquals(1, repo.linkCallCount)
        }

    @Test
    fun initializeRevenueCatCallsSdkWithCorrectArgs() =
        runTest {
            val sdk = FakeRevenueCatSdk()

            InitializeRevenueCatUseCase(sdk)("api_key", "user_42")

            assertEquals(1, sdk.initializeCallCount)
            assertEquals("user_42", sdk.lastInitializedUserId)
        }

    @Test
    fun initializeRevenueCatWorksWithNullUserId() =
        runTest {
            val sdk = FakeRevenueCatSdk()

            InitializeRevenueCatUseCase(sdk)("api_key", null)

            assertEquals(1, sdk.initializeCallCount)
            assertEquals(null, sdk.lastInitializedUserId)
        }
}
