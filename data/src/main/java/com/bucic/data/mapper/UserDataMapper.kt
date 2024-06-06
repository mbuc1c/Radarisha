package com.bucic.data.mapper

import com.bucic.data.entities.user.UserDbData
import com.bucic.data.entities.user.UserFSData
import com.bucic.domain.entities.UserEntity

fun UserEntity.toFSData() = UserFSData(
    username = username,
    password = password
)

fun UserEntity.toDbData() = UserDbData(
    uid = uid,
    username = username,
    password = password
)