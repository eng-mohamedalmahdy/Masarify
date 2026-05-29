package tech.lightfeather.masarify.page.paywall

internal sealed interface PaywallPageIntent {
    data object LoadStatus : PaywallPageIntent

    data object PurchasePro : PaywallPageIntent

    data object RestorePurchases : PaywallPageIntent

    data object Dismiss : PaywallPageIntent
}
