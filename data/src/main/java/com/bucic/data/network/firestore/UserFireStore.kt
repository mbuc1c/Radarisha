package com.bucic.data.network.firestore

import com.bucic.data.entities.user.UserFSData
import com.google.firebase.firestore.QuerySnapshot

interface UserFireStore {
    suspend fun createUser(user: UserFSData)
    suspend fun getUserByUsername(username: String): QuerySnapshot
}