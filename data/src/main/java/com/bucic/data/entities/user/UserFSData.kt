package com.bucic.data.entities.user

import com.bucic.domain.entities.UserEntity

data class UserFSData(
    val username: String,
    val password: String
)

fun UserFSData.toDomain(uid: String) = UserEntity(
    uid = uid,
    username = username,
    password = password,
    stayLoggedIn = null
)
