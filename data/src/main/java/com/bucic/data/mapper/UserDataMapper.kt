package com.bucic.data.mapper

import com.bucic.data.entities.radar.RadarFSData
import com.bucic.data.entities.user.UserDbData
import com.bucic.data.entities.user.UserFSData
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.UserEntity
import com.google.firebase.Timestamp

fun UserEntity.toFSData() = UserFSData(
    username = username,
    password = password
)

fun UserEntity.toDbData() = UserDbData(
    uid = uid,
    username = username,
    password = password
)