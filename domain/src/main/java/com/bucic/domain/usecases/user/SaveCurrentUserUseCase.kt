package com.bucic.domain.usecases.user

import com.bucic.domain.entities.UserEntity
import com.bucic.domain.repository.UserRepository

class SaveCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    suspend fun invoke(user: UserEntity) = userRepository.saveCurrentUser(user)
}