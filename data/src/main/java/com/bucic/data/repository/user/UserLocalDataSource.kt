package com.bucic.data.repository.user

import com.bucic.data.database.user.dao.UserDao
import com.bucic.data.entities.user.toDomain
import com.bucic.data.mapper.toDbData
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.Result

class UserLocalDataSource(
    private val userDao: UserDao
) : UserDataSource.Local {

    override suspend fun saveCurrentUser(user: UserEntity) {
        userDao.deleteAll()
        userDao.insert(user.toDbData())
    }

    override suspend fun getCurrentUser(): Result<UserEntity> = try {
        val result = userDao.getRegisteredUser()
        Result.Success(result.toDomain())
    } catch (e: Exception) {
        Result.Error(e.message.toString())
    }

    override suspend fun deleteAll() {
        userDao.deleteAll()
    }

}