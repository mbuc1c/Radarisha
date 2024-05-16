package com.bucic.domain.repository

import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.Result

interface UserRepository {

    suspend fun createUser(user: UserEntity)
    suspend fun getUserByUsername(username: String): Result<UserEntity>
    suspend fun saveCurrentUser(user: UserEntity)
    suspend fun getCurrentUser(): Result<UserEntity>
}