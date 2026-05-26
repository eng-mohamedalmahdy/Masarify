package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.ResetPasswordUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.auth.resetpassword.ResetPasswordPageViewModel

internal fun buildResetPasswordViewModel(
    authRepository: FakeAuthRepository = FakeAuthRepository(),
    navigator: Navigator = CapturingNavigator(),
): ResetPasswordPageViewModel =
    ResetPasswordPageViewModel(
        resetPasswordUseCase = ResetPasswordUseCase(authRepository),
        navigator = navigator,
    )
