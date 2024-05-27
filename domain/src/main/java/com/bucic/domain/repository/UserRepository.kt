package com.bucic.domain.repository

import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.Result

interface UserRepository {
    // Remote usage
    suspend fun createUser(user: UserEntity)
    suspend fun getUserByUsername(username: String): Result<UserEntity>

    // Local usage
    suspend fun saveCurrentUser(user: UserEntity)
    suspend fun getCurrentUser(): Result<UserEntity>
}