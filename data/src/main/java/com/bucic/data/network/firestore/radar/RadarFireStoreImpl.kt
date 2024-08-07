package com.bucic.data.network.firestore.radar

import android.util.Log
import com.bucic.data.entities.radar.RadarFSData
import com.bucic.data.entities.radar.RadarReliabilityVoteFSData
import com.bucic.data.exception.NoResultFoundException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RadarFireStoreImpl @Inject constructor(
    private val db: FirebaseFirestore
) : RadarFireStore {

    override suspend fun addRadar(radar: RadarFSData) {
        db.collection("radars")
            .add(radar)
    }

    override suspend fun getAllRadars(): QuerySnapshot {
        val result = db.collection("radars")
            .get()
            .await()

        if (result.isEmpty) {
            throw NoResultFoundException("No radars found")
        } else return result
    }

    override suspend fun getRadarByUid(radarUid: String): DocumentSnapshot {
        val result = db.collection("radars")
            .document(radarUid)
            .get()
            .await()

        if (!result.exists()) {
            throw NoResultFoundException("No radar found with uid: $radarUid")
        } else return result
    }

    override suspend fun deleteRadar(radarUid: String) {
        db.collection("radars")
            .document(radarUid)
            .delete()
    }

    override suspend fun updateRadar(radar: RadarFSData, radarUid: String) {
        db.collection("radars")
            .document(radarUid)
            .update(
                mapOf(
                    "lat" to radar.lat,
                    "lng" to radar.lng,
                    "type" to radar.type,
                    "speed" to radar.speed,
                    "updatedAt" to radar.updatedAt
                )
            )
    }

    override fun vote(radarReliabilityVote: RadarReliabilityVoteFSData, radarUid: String) {
        val radarRef = db.collection("radars").document(radarUid)
        val voteSubcollectionRef = radarRef.collection("reliability")

        voteSubcollectionRef.whereEqualTo("voterUid", radarReliabilityVote.voterUid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Document does not exist, add a new one
                    postVote(voteSubcollectionRef, radarReliabilityVote)
                } else {
                    // Document exists, update the existing one
                    val docId = documents.documents[0].id
                    updateVote(voteSubcollectionRef, docId, radarReliabilityVote)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("VoteTag", "Error getting documents: ", exception)
            }
    }

    private fun postVote(voteSubcollectionRef: CollectionReference, radarReliabilityVote: RadarReliabilityVoteFSData) {
        voteSubcollectionRef.add(mapOf(
            "voterUid" to radarReliabilityVote.voterUid,
            "vote" to radarReliabilityVote.vote,
            "createdAt" to radarReliabilityVote.createdAt,
            "updatedAt" to null
        ))
            .addOnSuccessListener { documentReference ->
                Log.d("VoteTag", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("VoteTag", "Error adding document", e)
            }
    }

    private fun updateVote(
        voteSubcollectionRef: CollectionReference,
        docId: String,
        radarReliabilityVote: RadarReliabilityVoteFSData
    ) {
        voteSubcollectionRef.document(docId)
            .update(
                mapOf(
                    "vote" to radarReliabilityVote.vote,
                    "updatedAt" to radarReliabilityVote.updatedAt
                )
            )
            .addOnSuccessListener {
                Log.d("VoteTag", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("VoteTag", "Error updating document", e)
            }
    }
}