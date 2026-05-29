package tech.lightfeather.masarify.page.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.domain.usecase.GetSubscriptionStatusUseCase
import tech.lightfeather.domain.usecase.PurchaseProUseCase
import tech.lightfeather.domain.usecase.RestorePurchasesUseCase
import tech.lightfeather.masarify.navigation.Navigator

class PaywallPageViewModel(
    private val getSubscriptionStatusUseCase: GetSubscriptionStatusUseCase,
    private val purchaseProUseCase: PurchaseProUseCase,
    private val restorePurchasesUseCase: RestorePurchasesUseCase,
    private val navigator: Navigator,
) : ViewModel() {
    private val _state = MutableStateFlow(PaywallPageState())
    internal val state: StateFlow<PaywallPageState> = _state

    internal fun onIntent(intent: PaywallPageIntent) {
        when (intent) {
            PaywallPageIntent.LoadStatus -> loadStatus()
            PaywallPageIntent.PurchasePro -> purchasePro()
            PaywallPageIntent.RestorePurchases -> restorePurchases()
            PaywallPageIntent.Dismiss -> navigator.navigateUp()
        }
    }

    private fun loadStatus() {
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            _state.value = _state.value.copy(isLoading = true)
            getSubscriptionStatusUseCase().collect {
                it.fold(
                    onSuccess = { status ->
                        _state.value =
                            _state.value.copy(
                                currentPlan = status.plan,
                                expiresAt = status.expiresAt,
                                isLoading = false,
                            )
                    },
                    onFailure = {
                        _state.value = _state.value.copy(isLoading = false)
                    }
                )
            }


        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun purchasePro() {
        if (_state.value.isPurchasing) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isPurchasing = true)
            runCatching { purchaseProUseCase() }
                .onSuccess {
                    it.fold(
                        onSuccess = { navigator.navigateUp() },
                        onFailure = { error ->
                            if (error.message.contains("cancelled")) {
                                // Silent — user cancelled
                            } else {
                                SnackbarService.sendErrorMessage(MR.strings.subscription_purchase_failure)
                            }
                        },
                    )
                }.onFailure {
                    SnackbarService.sendErrorMessage(MR.strings.subscription_purchase_failure)
                }
            _state.value = _state.value.copy(isPurchasing = false)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun restorePurchases() {
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            _state.value = _state.value.copy(isPurchasing = true)
            runCatching { restorePurchasesUseCase() }
                .onSuccess {
                    it.fold(
                        onSuccess = {
                            SnackbarService.sendSuccessMessage(MR.strings.subscription_restore_success)
                            navigator.navigateUp()
                        },
                        onFailure = {
                            SnackbarService.sendErrorMessage(MR.strings.subscription_restore_failure)
                        },
                    )
                }.onFailure {
                    SnackbarService.sendErrorMessage(MR.strings.subscription_restore_failure)
                }
            _state.value = _state.value.copy(isPurchasing = false)
        }
    }
}
