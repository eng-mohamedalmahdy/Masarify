package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): DomainResult<Unit> =
        authRepository.login(email, password)
}

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): DomainResult<Unit> =
        authRepository.register(name, email, password)
}

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): DomainResult<Unit> = authRepository.logout()
}

class IsAuthenticatedUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): Boolean = authRepository.isAuthenticated()
}
