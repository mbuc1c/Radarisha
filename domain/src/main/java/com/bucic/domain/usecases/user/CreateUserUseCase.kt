package com.bucic.domain.usecases.user

import com.bucic.domain.entities.UserEntity
import com.bucic.domain.repository.UserRepository

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: UserEntity) = userRepository.createUser(user)
}