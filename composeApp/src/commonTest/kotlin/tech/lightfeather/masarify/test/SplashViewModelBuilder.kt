package tech.lightfeather.masarify.test

import tech.lightfeather.domain.usecase.AcquireAndUpdateFcmUseCase
import tech.lightfeather.domain.usecase.RegisterDeviceTokenUseCase
import tech.lightfeather.domain.usecase.SeedApplicationData
import tech.lightfeather.domain.usecase.SeedDefaultBankNames
import tech.lightfeather.domain.usecase.SeedDefaultCategories
import tech.lightfeather.domain.usecase.SeedDefaultCurrencies
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.splash.SplashPageViewModel

internal fun buildSplashViewModel(
    navigator: Navigator = CapturingNavigator(),
    isLoggedIn: Boolean = false,
    isOnboardingComplete: Boolean = false,
): SplashPageViewModel {
    val userRepo =
        FakeUserRepository(
            isLoggedInValue = isLoggedIn,
            isOnboardingCompleteValue = isOnboardingComplete,
            isDataSeededValue = true,
        )
    return SplashPageViewModel(
        navigator = navigator,
        userRepository = userRepo,
        seedApplicationData =
            SeedApplicationData(
                userRepository = userRepo,
                seedDefaultCategories = SeedDefaultCategories(FakeCategoryRepository()),
                seedDefaultCurrencies = SeedDefaultCurrencies(FakeCurrencyRepository()),
                seedDefaultBankNames = SeedDefaultBankNames(FakeBankNameRepository()),
            ),
        acquireAndUpdateFcmUseCase =
            AcquireAndUpdateFcmUseCase(
                platform = FakePlatform,
                fcmHelper = FakeFCMHelper,
                userRepository = userRepo,
                registerDeviceTokenUseCase = RegisterDeviceTokenUseCase(FakeDeviceTokenRepository()),
            ),
    )
}
