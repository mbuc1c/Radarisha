package com.bucic.domain.usecases.user

import com.bucic.domain.repository.UserRepository

class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    suspend fun invoke() = userRepository.getCurrentUser()
}