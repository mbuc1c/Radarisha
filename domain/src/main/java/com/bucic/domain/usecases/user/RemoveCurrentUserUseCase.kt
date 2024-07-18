package com.bucic.domain.usecases.user

import com.bucic.domain.repository.UserRepository

class RemoveCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.removeCurrentUser()
}