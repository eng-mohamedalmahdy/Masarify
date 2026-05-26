package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.VerifyEmailUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.auth.verifyemail.VerifyEmailPageViewModel

internal fun buildVerifyEmailViewModel(
    authRepository: FakeAuthRepository = FakeAuthRepository(),
    navigator: Navigator = CapturingNavigator(),
): VerifyEmailPageViewModel =
    VerifyEmailPageViewModel(
        verifyEmailUseCase = VerifyEmailUseCase(authRepository),
        navigator = navigator,
    )
