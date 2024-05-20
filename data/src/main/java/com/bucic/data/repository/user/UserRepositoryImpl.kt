package com.bucic.data.repository.user

import com.bucic.domain.entities.UserEntity
import com.bucic.domain.repository.UserRepository
import com.bucic.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val remote: UserDataSource.Remote
) : UserRepository {
    override suspend fun createUser(user: UserEntity) = withContext(Dispatchers.IO) {
        remote.createUser(user)
    }

    override suspend fun getUserByUsername(username: String): Result<UserEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun saveCurrentUser(user: UserEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUser(): Result<UserEntity> {
        TODO("Not yet implemented")
    }
}