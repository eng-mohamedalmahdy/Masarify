package com.lightfeather.masarify.page.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.data.util.IoDispatcher
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.domain.repository.AccountRepository
import com.lightfeather.domain.repository.UserRepository
import com.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import com.lightfeather.domain.usecase.LoginUseCase
import com.lightfeather.domain.usecase.PullRemoteDeltaUseCase
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.DashboardRoute
import com.lightfeather.masarify.navigation.routes.OnBoardingRoute
import com.lightfeather.masarify.navigation.routes.RegisterRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LoginPageViewModel(
    private val loginUseCase: LoginUseCase,
    private val navigator: Navigator,
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val pullRemoteDeltaUseCase: PullRemoteDeltaUseCase,
    private val drainOutboxQueueUseCase: DrainOutboxQueueUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginPageState())
    internal val state: StateFlow<LoginPageState> = _state

    internal fun onIntent(intent: LoginPageIntent) {
        when (intent) {
            is LoginPageIntent.UpdateEmail -> _state.value = _state.value.copy(email = intent.email)
            is LoginPageIntent.UpdatePassword -> _state.value = _state.value.copy(password = intent.password)
            LoginPageIntent.NavigateToRegister -> navigator.navigate(RegisterRoute)
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
                    pullRemoteDeltaUseCase()
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

    private suspend fun navigateAfterAuth() {
        val isUserDataInitialized =
            userRepository.getUserData().foldResult(
                onSuccess = { it != null },
                onFailure = { false },
            )
        val hasAccounts =
            accountRepository.getAccounts().foldResult(
                onSuccess = { it.map { it.isNotEmpty() } },
                onFailure = { flowOf(false) },
            )
        hasAccounts.collect { hasBankAccounts ->
            if (hasBankAccounts && isUserDataInitialized) {
                navigator.navigateAndClearBackStack(DashboardRoute)
            } else {
                navigator.navigateAndClearBackStack(OnBoardingRoute)
            }
        }
    }
}
