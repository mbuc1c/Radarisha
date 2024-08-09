package com.bucic.data.network.firestore.user

import android.util.Log
import com.bucic.data.entities.user.UserFSData
import com.bucic.data.exception.NoResultFoundException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserFireStoreImpl @Inject constructor(
    private val db: FirebaseFirestore
) : UserFireStore {
    override suspend fun createUser(user: UserFSData) {
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("UserFireStore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.w("UserFireStore", "Error adding document", e)
            }
    }

    override suspend fun getUserByUsernameAndPassword(username: String, password: String): QuerySnapshot {
        val result = db.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .await()
        if (result.isEmpty) {
            throw NoResultFoundException("Invalid username or password!")
        } else return result
    }
}