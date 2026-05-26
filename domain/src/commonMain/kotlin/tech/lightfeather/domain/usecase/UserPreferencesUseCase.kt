package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.AppLanguage
import tech.lightfeather.domain.model.AppLanguages
import tech.lightfeather.domain.repository.UserRepository

class ToggleDarkMode(
    private val repository: UserRepository,
) {
    operator fun invoke() = repository.toggleDarkMode()
}

class GetUserDarkMode(
    private val repository: UserRepository,
) {
    operator fun invoke() = repository.isDarkMode()
}

class SetLanguage(
    private val repository: UserRepository,
) {
    operator fun invoke(language: AppLanguage) = repository.setAppLanguage(language)
}

class GetUserLanguage(
    private val repository: UserRepository,
) {
    operator fun invoke() = repository.getAppLanguage() ?: AppLanguages.Arabic
}

class GetUserSavedColors(
    private val repository: UserRepository,
) {
    operator fun invoke() = repository.getUserSavedColors()
}

class SaveUserColor(
    private val repository: UserRepository,
) {
    operator fun invoke(color: String) = repository.saveColor(color)
}

class IsOnboardingComplete(
    private val repository: UserRepository,
) {
    operator fun invoke() = repository.isOnboardingComplete()
}

class MarkOnboardingComplete(
    private val repository: UserRepository,
) {
    operator fun invoke() = repository.markOnboardingComplete()
}
