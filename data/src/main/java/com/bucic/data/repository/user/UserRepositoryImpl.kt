package com.bucic.data.repository.user

import com.bucic.domain.entities.UserEntity
import com.bucic.domain.repository.UserRepository
import com.bucic.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val remote: UserDataSource.Remote,
    private val local: UserDataSource.Local
) : UserRepository {
    override suspend fun createUser(user: UserEntity) = withContext(Dispatchers.IO) {
        remote.createUser(user)
    }

    override suspend fun getUserByUsernameAndPassword(username: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        remote.getUserByUsernameAndPassword(username, password)
    }

    override suspend fun saveCurrentUser(user: UserEntity, stayLoggedIn: Boolean) = withContext(Dispatchers.IO) {
        local.saveCurrentUser(user, stayLoggedIn)
    }

    override suspend fun getCurrentUser(): Result<UserEntity> = withContext(Dispatchers.IO) {
        local.getCurrentUser()
    }

    override suspend fun removeCurrentUser() = withContext(Dispatchers.IO) {
        local.deleteAll()
    }
}