package tech.lightfeather.masarify.page.auth.resetpassword

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
import tech.lightfeather.domain.usecase.ResetPasswordUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.routes.LoginRoute

class ResetPasswordPageViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val navigator: Navigator,
) : ViewModel() {
    private val _state = MutableStateFlow(ResetPasswordPageState())
    internal val state: StateFlow<ResetPasswordPageState> = _state

    internal fun onIntent(intent: ResetPasswordPageIntent) {
        when (intent) {
            is ResetPasswordPageIntent.UpdateNewPassword ->
                _state.value = _state.value.copy(newPassword = intent.password)

            is ResetPasswordPageIntent.UpdateConfirmPassword ->
                _state.value = _state.value.copy(confirmPassword = intent.password)

            is ResetPasswordPageIntent.Submit -> submitResetPassword(intent.token)

            ResetPasswordPageIntent.NavigateToLogin ->
                navigator.navigateAndClearBackStack(LoginRoute)
        }
    }

    private fun submitResetPassword(token: String) {
        val snapshot = _state.value
        if (snapshot.newPassword.isBlank()) {
            SnackbarService.sendErrorMessage(MR.strings.password_required)
            return
        }
        if (snapshot.newPassword != snapshot.confirmPassword) {
            SnackbarService.sendErrorMessage(MR.strings.passwords_do_not_match)
            return
        }
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            _state.value = _state.value.copy(isLoading = true)
            when (resetPasswordUseCase(token, snapshot.newPassword)) {
                is DomainResult.Success -> {
                    SnackbarService.sendSuccessMessage(MR.strings.reset_password_success)
                    navigator.navigateAndClearBackStack(LoginRoute)
                }
                is DomainResult.Failure -> {
                    SnackbarService.sendErrorMessage(MR.strings.reset_password_failure)
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }
}
