package com.lightfeather.domain.usecase

import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.UserData
import com.lightfeather.domain.repository.UserRepository

class UpsertUserData(private val repository: UserRepository){
    suspend operator fun invoke(userData: UserData) : DomainResult<Unit> = repository.upsertUserData(userData)
}