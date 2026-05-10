package tech.lightfeather.domain.usecase

import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.UserData
import tech.lightfeather.domain.repository.UserRepository

class UpsertUserData(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(userData: UserData): DomainResult<Unit> = repository.upsertUserData(userData)
}
