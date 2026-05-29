@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.paywall

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.domain.model.SubscriptionPlan
import tech.lightfeather.masarify.test.CapturingNavigator
import tech.lightfeather.masarify.test.FakeRevenueCatSdk
import tech.lightfeather.masarify.test.FakeSubscriptionRepository
import tech.lightfeather.masarify.test.buildPaywallViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaywallPageViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadStatusSetsFreeCurrentPlan() =
        runTest {
            val vm = buildPaywallViewModel(subscriptionRepo = FakeSubscriptionRepository(isProActive = false))

            vm.onIntent(PaywallPageIntent.LoadStatus)

            assertEquals(SubscriptionPlan.FREE, vm.state.value.currentPlan)
        }

    @Test
    fun loadStatusSetsProCurrentPlan() =
        runTest {
            val vm = buildPaywallViewModel(subscriptionRepo = FakeSubscriptionRepository(isProActive = true))

            vm.onIntent(PaywallPageIntent.LoadStatus)

            assertEquals(SubscriptionPlan.PRO, vm.state.value.currentPlan)
        }

    @Test
    fun loadStatusSetsIsLoadingFalseAfterCompletion() =
        runTest {
            val vm = buildPaywallViewModel()

            vm.onIntent(PaywallPageIntent.LoadStatus)

            assertFalse(vm.state.value.isLoading)
        }

    @Test
    fun purchaseProSuccessCallsNavigateUp() =
        runTest {
            val navigator = CapturingNavigator()
            val vm =
                buildPaywallViewModel(
                    revenueCatSdk = FakeRevenueCatSdk(shouldSucceed = true),
                    navigator = navigator,
                )

            vm.onIntent(PaywallPageIntent.PurchasePro)

            assertTrue(navigator.navigateUpCallCount > 0)
        }

    @Test
    fun purchaseProCancelledIsSilentAndDoesNotNavigate() =
        runTest {
            val navigator = CapturingNavigator()
            val vm =
                buildPaywallViewModel(
                    revenueCatSdk = FakeRevenueCatSdk(shouldSucceed = false, shouldCancel = true),
                    navigator = navigator,
                )

            vm.onIntent(PaywallPageIntent.PurchasePro)

            assertEquals(0, navigator.navigateUpCallCount)
        }

    @Test
    fun purchaseProOtherFailureDoesNotNavigate() =
        runTest {
            val navigator = CapturingNavigator()
            val vm =
                buildPaywallViewModel(
                    revenueCatSdk = FakeRevenueCatSdk(shouldSucceed = false, shouldCancel = false),
                    navigator = navigator,
                )

            vm.onIntent(PaywallPageIntent.PurchasePro)

            assertEquals(0, navigator.navigateUpCallCount)
        }

    @Test
    fun isPurchasingIsFalseAfterPurchaseCompletes() =
        runTest {
            val vm = buildPaywallViewModel()

            vm.onIntent(PaywallPageIntent.PurchasePro)

            assertFalse(vm.state.value.isPurchasing)
        }

    @Test
    fun restorePurchasesSuccessCallsNavigateUp() =
        runTest {
            val navigator = CapturingNavigator()
            val vm =
                buildPaywallViewModel(
                    revenueCatSdk = FakeRevenueCatSdk(shouldSucceed = true),
                    navigator = navigator,
                )

            vm.onIntent(PaywallPageIntent.RestorePurchases)

            assertTrue(navigator.navigateUpCallCount > 0)
        }

    @Test
    fun restorePurchasesFailureDoesNotNavigate() =
        runTest {
            val navigator = CapturingNavigator()
            val vm =
                buildPaywallViewModel(
                    revenueCatSdk = FakeRevenueCatSdk(shouldSucceed = false),
                    navigator = navigator,
                )

            vm.onIntent(PaywallPageIntent.RestorePurchases)

            assertEquals(0, navigator.navigateUpCallCount)
        }

    @Test
    fun dismissCallsNavigateUp() =
        runTest {
            val navigator = CapturingNavigator()
            val vm = buildPaywallViewModel(navigator = navigator)

            vm.onIntent(PaywallPageIntent.Dismiss)

            assertEquals(1, navigator.navigateUpCallCount)
        }
}
