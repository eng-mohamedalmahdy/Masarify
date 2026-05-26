package tech.lightfeather.masarify.page.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.usecase.ForgotPasswordUseCase
import tech.lightfeather.masarify.navigation.Navigator

class ForgotPasswordPageViewModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val navigator: Navigator,
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordPageState())
    internal val state: StateFlow<ForgotPasswordPageState> = _state

    internal fun onIntent(intent: ForgotPasswordPageIntent) {
        when (intent) {
            is ForgotPasswordPageIntent.UpdateEmail ->
                _state.value = _state.value.copy(email = intent.email)

            ForgotPasswordPageIntent.Submit -> submitForgotPassword()

            ForgotPasswordPageIntent.NavigateBack -> navigator.navigateUp()
        }
    }

    private fun submitForgotPassword() {
        val email = _state.value.email
        if (email.isBlank()) {
            SnackbarService.sendErrorMessage(MR.strings.email_required)
            return
        }
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            _state.value = _state.value.copy(isLoading = true)
            when (forgotPasswordUseCase(email)) {
                is DomainResult.Success ->
                    _state.value = _state.value.copy(isLoading = false, isSent = true)
                is DomainResult.Failure -> {
                    SnackbarService.sendErrorMessage(MR.strings.forgot_password_failure)
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }
}
