package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.GetSubscriptionStatusUseCase
import tech.lightfeather.domain.usecase.PurchaseProUseCase
import tech.lightfeather.domain.usecase.RestorePurchasesUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.paywall.PaywallPageViewModel

internal fun buildPaywallViewModel(
    subscriptionRepo: FakeSubscriptionRepository = FakeSubscriptionRepository(),
    revenueCatSdk: FakeRevenueCatSdk = FakeRevenueCatSdk(),
    navigator: Navigator = CapturingNavigator(),
): PaywallPageViewModel =
    PaywallPageViewModel(
        getSubscriptionStatusUseCase = GetSubscriptionStatusUseCase(subscriptionRepo),
        purchaseProUseCase = PurchaseProUseCase(revenueCatSdk, subscriptionRepo),
        restorePurchasesUseCase = RestorePurchasesUseCase(revenueCatSdk, subscriptionRepo),
        navigator = navigator,
    )
