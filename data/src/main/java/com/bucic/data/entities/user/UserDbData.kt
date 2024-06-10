package com.bucic.data.entities.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bucic.domain.entities.UserEntity

@Entity(tableName = "registered_user")
data class UserDbData(
    @PrimaryKey val uid: String,
    val username: String,
    val password: String
)

fun UserDbData.toDomain() = UserEntity(
    uid = uid,
    username = username,
    password = password
)
