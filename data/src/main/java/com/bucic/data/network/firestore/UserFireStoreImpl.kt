package com.bucic.data.network.firestore

import android.util.Log
import com.bucic.data.entities.user.UserFSData
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserFireStoreImpl @Inject constructor(
    private val db: FirebaseFirestore
) : UserFireStore {
    override suspend fun createUser(user: UserFSData) {
        // TODO: res str value
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("UserFireStore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.w("UserFireStore", "Error adding document", e)
            }
    }

    override suspend fun getUserByUsername(username: String): UserFSData {
        TODO("Not yet implemented")
    }

}