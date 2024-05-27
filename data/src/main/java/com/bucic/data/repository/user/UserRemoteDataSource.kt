package com.bucic.data.repository.user

import android.util.Log
import com.bucic.data.entities.user.UserFSData
import com.bucic.data.entities.user.toDomain
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

    override suspend fun getUserByUsername(username: String): Result<UserEntity> = try {
        val result = userFireStore.getUserByUsername(username)
        val user = result.documents[0]

        Log.i("customTag", "${user.data}")
        Result.Success(UserFSData(
            username = user.data!!["username"].toString(),
            password = user.data!!["password"].toString()
        ).toDomain(user.id))
    } catch (e: Exception) {
        Log.e("customTag", "Exception: ", e)
        Result.Error(e.message as String)
    }
}