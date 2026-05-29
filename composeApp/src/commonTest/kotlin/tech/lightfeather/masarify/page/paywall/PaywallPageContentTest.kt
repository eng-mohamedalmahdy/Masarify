package tech.lightfeather.masarify.page.paywall

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.domain.model.SubscriptionPlan
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class PaywallPageContentTest {
    @Test
    fun subscribeButtonIsDisplayedWhenNotPurchasing() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    PaywallPageContent(
                        state =
                            PaywallPageState(
                                currentPlan = SubscriptionPlan.FREE,
                                isPurchasing = false,
                                isLoading = false,
                            ),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("paywall_subscribe_button").assertIsDisplayed()
        }

    @Test
    fun subscribeButtonIsHiddenWhenPurchasing() =
        runComposeUiTest {
            setContent {
                AppTheme(useDarkTheme = false) {
                    PaywallPageContent(
                        state = PaywallPageState(isPurchasing = true),
                        onIntent = {},
                    )
                }
            }

            onNodeWithTag("paywall_subscribe_button").assertDoesNotExist()
        }

    @Test
    fun clickingSubscribeButtonFiresPurchaseProIntent() =
        runComposeUiTest {
            val capturedIntents = mutableListOf<PaywallPageIntent>()
            setContent {
                AppTheme(useDarkTheme = false) {
                    PaywallPageContent(
                        state = PaywallPageState(isPurchasing = false, isLoading = false),
                        onIntent = capturedIntents::add,
                    )
                }
            }

            onNodeWithTag("paywall_subscribe_button").performClick()

            assertTrue(capturedIntents.contains(PaywallPageIntent.PurchasePro))
        }
}
