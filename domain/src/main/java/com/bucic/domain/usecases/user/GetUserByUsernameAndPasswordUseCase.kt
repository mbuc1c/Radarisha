package com.bucic.domain.usecases.user

import com.bucic.domain.repository.UserRepository

class GetUserByUsernameAndPasswordUseCase(
    private val userRepository: UserRepository
) {
    suspend fun invoke(username: String, password: String) = userRepository.getUserByUsernameAndPassword(username, password)
}