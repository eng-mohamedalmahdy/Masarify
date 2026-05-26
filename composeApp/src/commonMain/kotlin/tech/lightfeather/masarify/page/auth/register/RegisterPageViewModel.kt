package tech.lightfeather.masarify.page.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import tech.lightfeather.domain.usecase.RegisterUseCase
import tech.lightfeather.domain.usecase.UploadLocalDataUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.routes.OnBoardingRoute

class RegisterPageViewModel(
    private val registerUseCase: RegisterUseCase,
    private val navigator: Navigator,
    private val uploadLocalDataUseCase: UploadLocalDataUseCase,
    private val drainOutboxQueueUseCase: DrainOutboxQueueUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterPageState())
    internal val state: StateFlow<RegisterPageState> = _state

    internal fun onIntent(intent: RegisterPageIntent) {
        when (intent) {
            is RegisterPageIntent.UpdateName -> _state.value = _state.value.copy(name = intent.name)
            is RegisterPageIntent.UpdateEmail -> _state.value = _state.value.copy(email = intent.email)
            is RegisterPageIntent.UpdatePassword -> _state.value = _state.value.copy(password = intent.password)
            is RegisterPageIntent.UpdateConfirmPassword ->
                _state.value = _state.value.copy(confirmPassword = intent.confirmPassword)
            RegisterPageIntent.NavigateToLogin -> navigator.navigateUp()
            RegisterPageIntent.Submit -> submitRegister()
        }
    }

    private fun submitRegister() {
        val snapshot = _state.value
        val validationError =
            when {
                snapshot.name.isBlank() -> MR.strings.name_required
                snapshot.email.isBlank() -> MR.strings.email_required
                snapshot.password.isBlank() -> MR.strings.password_required
                snapshot.password != snapshot.confirmPassword -> MR.strings.passwords_do_not_match
                else -> null
            }
        if (validationError != null) {
            SnackbarService.sendErrorMessage(validationError)
            return
        }
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            _state.value = _state.value.copy(isLoading = true)
            registerUseCase(snapshot.name, snapshot.email, snapshot.password).foldSuspend(
                onSuccess = {
                    SnackbarService.sendSuccessMessage(MR.strings.register_success)
                    uploadLocalDataUseCase()
                    drainOutboxQueueUseCase()
                    navigator.navigateAndClearBackStack(OnBoardingRoute)
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.register_failure)
                },
            )
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
