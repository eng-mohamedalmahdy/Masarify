package tech.lightfeather.masarify.page.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.domain.usecase.AcquireAndUpdateFcmUseCase
import tech.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import tech.lightfeather.domain.usecase.GetSubscriptionStatusUseCase
import tech.lightfeather.domain.usecase.LoginUseCase
import tech.lightfeather.domain.usecase.PullRemoteDeltaUseCase
import tech.lightfeather.domain.usecase.UploadLocalDataUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.routes.DashboardRoute
import tech.lightfeather.masarify.navigation.routes.ForgotPasswordRoute
import tech.lightfeather.masarify.navigation.routes.RegisterRoute

class LoginPageViewModel(
    private val loginUseCase: LoginUseCase,
    private val navigator: Navigator,
    private val pullRemoteDeltaUseCase: PullRemoteDeltaUseCase,
    private val drainOutboxQueueUseCase: DrainOutboxQueueUseCase,
    private val uploadLocalDataUseCase: UploadLocalDataUseCase,
    private val acquireAndUpdateFcmUseCase: AcquireAndUpdateFcmUseCase,
    private val getSubscriptionStatusUseCase: GetSubscriptionStatusUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginPageState())
    internal val state: StateFlow<LoginPageState> = _state

    internal fun onIntent(intent: LoginPageIntent) {
        when (intent) {
            is LoginPageIntent.UpdateEmail -> _state.value = _state.value.copy(email = intent.email)
            is LoginPageIntent.UpdatePassword -> _state.value = _state.value.copy(password = intent.password)
            LoginPageIntent.NavigateToRegister -> navigator.navigate(RegisterRoute)
            LoginPageIntent.NavigateToForgotPassword -> navigator.navigate(ForgotPasswordRoute)
            LoginPageIntent.Submit -> submitLogin()
        }
    }

    private fun submitLogin() {
        val snapshot = _state.value
        if (snapshot.email.isBlank()) {
            SnackbarService.sendErrorMessage(MR.strings.email_required)
            return
        }
        if (snapshot.password.isBlank()) {
            SnackbarService.sendErrorMessage(MR.strings.password_required)
            return
        }
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            _state.value = _state.value.copy(isLoading = true)
            loginUseCase(snapshot.email, snapshot.password).foldSuspend(
                onSuccess = {
                    SnackbarService.sendSuccessMessage(MR.strings.login_success)
                    acquireAndUpdateFcmUseCase()
                    getSubscriptionStatusUseCase() // pre-warm subscription cache
                    pullRemoteDeltaUseCase()
                    uploadLocalDataUseCase()
                    drainOutboxQueueUseCase()
                    navigateAfterAuth()
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.login_failure)
                },
            )
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun navigateAfterAuth() {
        navigator.navigateAndClearBackStack(DashboardRoute)
    }
}
