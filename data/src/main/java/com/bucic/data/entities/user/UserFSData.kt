package com.bucic.data.entities.user

import com.bucic.domain.entities.UserEntity

data class UserFSData(
    val id: String,
    val username: String,
    val password: String
)

fun UserFSData.toDomain() = UserEntity(
    id = id,
    username = username,
    password = password
)
