package com.bucic.data.repository.user

import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.Result

interface UserDataSource {

    interface Remote {
        suspend fun createUser(user: UserEntity)
        suspend fun getUserByUsernameAndPassword(username: String, password: String): Result<UserEntity>
    }


    interface Local {
        suspend fun saveCurrentUser(user: UserEntity, stayLoggedIn: Boolean)
        suspend fun getCurrentUser(): Result<UserEntity>
        suspend fun deleteAll()
    }
}