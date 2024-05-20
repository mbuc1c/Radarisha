package com.bucic.data.repository.user

import com.bucic.data.entities.user.UserFSData
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.Result

interface UserDataSource {

    interface Remote {
        suspend fun createUser(user: UserEntity)
        suspend fun getUserByUsername(username: String): Result<UserEntity>
    }


    interface Local {

    }
}