package com.bucic.data.entities.user

import com.bucic.domain.entities.UserEntity

data class UserFSData(
//    val uid: String,
    val username: String,
    val password: String
)

fun UserFSData.toDomain(userId: String) = UserEntity(
    id = userId,
    username = username,
    password = password
)
