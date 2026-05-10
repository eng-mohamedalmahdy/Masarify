package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): DomainResult<Unit> = authRepository.login(email, password)
}

class RegisterUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
    ): DomainResult<Unit> = authRepository.register(name, email, password)
}

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val clearLocalDataUseCase: ClearLocalDataUseCase,
) {
    suspend operator fun invoke(): DomainResult<Unit> {
        clearLocalDataUseCase()
        return authRepository.logout()
    }
}

class IsAuthenticatedUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Boolean = authRepository.isAuthenticated()
}
