package com.lightfeather.domain.usecase

import com.lightfeather.domain.repository.UserRepository

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
