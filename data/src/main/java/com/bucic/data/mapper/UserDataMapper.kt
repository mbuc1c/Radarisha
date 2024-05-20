package com.bucic.data.mapper

import com.bucic.data.entities.user.UserFSData
import com.bucic.domain.entities.UserEntity

fun UserEntity.toFSData() = UserFSData(
    id = id,
    username = username,
    password = password
)