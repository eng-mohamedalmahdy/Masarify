package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.repository.UserRepository


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
    operator fun invoke() = repository.getAppLanguage() ?: AppLanguage.Arabic
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
