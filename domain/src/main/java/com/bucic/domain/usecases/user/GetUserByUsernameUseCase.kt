package com.bucic.domain.usecases.user

import com.bucic.domain.repository.UserRepository

class GetUserByUsernameUseCase(
    private val userRepository: UserRepository
) {
    suspend fun invoke(username: String) = userRepository.getUserByUsername(username)
}