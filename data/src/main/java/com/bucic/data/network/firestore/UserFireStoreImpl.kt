package com.bucic.data.network.firestore

import android.util.Log
import com.bucic.data.entities.user.UserFSData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserFireStoreImpl @Inject constructor(
    private val db: FirebaseFirestore
) : UserFireStore {
    override suspend fun createUser(user: UserFSData) {
        // TODO: res str value i provjeri jel radi (izmjena ruleova)
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("UserFireStore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.w("UserFireStore", "Error adding document", e)
            }
    }

    override suspend fun getUserByUsernameAndPassword(username: String, password: String): QuerySnapshot = db.collection("users")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .await()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    Log.d("customTag", "${document.id} => ${document.data}")
//                }
//            }.addOnFailureListener { exception ->
//                Log.w("customTag", "Error getting documents: ", exception)
//            }
}