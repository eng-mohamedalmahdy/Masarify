package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.ForgotPasswordUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.auth.forgotpassword.ForgotPasswordPageViewModel

internal fun buildForgotPasswordViewModel(
    authRepository: FakeAuthRepository = FakeAuthRepository(),
    navigator: Navigator = CapturingNavigator(),
): ForgotPasswordPageViewModel =
    ForgotPasswordPageViewModel(
        forgotPasswordUseCase = ForgotPasswordUseCase(authRepository),
        navigator = navigator,
    )
