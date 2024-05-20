package com.bucic.data.network.firestore

import com.bucic.data.entities.user.UserFSData

interface UserFireStore {
    suspend fun createUser(user: UserFSData)
    suspend fun getUserByUsername(username: String): UserFSData
}