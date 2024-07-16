package com.bucic.domain.entities

data class UserEntity(
    val uid: String,
    val username: String,
    val password: String,
    var stayLoggedIn: Boolean?
)