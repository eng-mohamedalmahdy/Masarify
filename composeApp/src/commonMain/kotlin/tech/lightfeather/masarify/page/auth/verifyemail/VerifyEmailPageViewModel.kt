package tech.lightfeather.masarify.page.auth.verifyemail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.usecase.VerifyEmailUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.routes.DashboardRoute

class VerifyEmailPageViewModel(
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val navigator: Navigator,
) : ViewModel() {
    private val _state = MutableStateFlow(VerifyEmailPageState())
    internal val state: StateFlow<VerifyEmailPageState> = _state

    internal fun onIntent(intent: VerifyEmailPageIntent) {
        when (intent) {
            is VerifyEmailPageIntent.Verify -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    _state.value = _state.value.copy(isLoading = true)
                    when (verifyEmailUseCase(intent.token)) {
                        is DomainResult.Success ->
                            _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                        is DomainResult.Failure ->
                            _state.value = _state.value.copy(isLoading = false, isError = true)
                    }
                }
            }

            VerifyEmailPageIntent.GoToDashboard -> {
                navigator.navigateAndClearBackStack(DashboardRoute)
            }
        }
    }
}
