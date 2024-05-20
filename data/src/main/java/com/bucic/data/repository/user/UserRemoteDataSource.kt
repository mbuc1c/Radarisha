package com.bucic.data.repository.user

import com.bucic.data.mapper.toFSData
import com.bucic.data.network.firestore.UserFireStore
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.Result

class UserRemoteDataSource(
    private val userFireStore: UserFireStore
) : UserDataSource.Remote {
    override suspend fun createUser(user: UserEntity) {
        userFireStore.createUser(user.toFSData())
    }

    override suspend fun getUserByUsername(username: String): Result<UserEntity> {
        TODO("Not yet implemented")
    }
}