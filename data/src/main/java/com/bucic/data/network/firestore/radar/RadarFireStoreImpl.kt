package com.bucic.data.network.firestore.radar

import android.util.Log
import com.bucic.data.entities.radar.RadarFSData
import com.bucic.data.entities.radar.RadarReliabilityVoteFSData
import com.bucic.data.exception.NoResultFoundException
import com.bucic.data.mapper.toRadarDomain
import com.bucic.data.mapper.toRadarReliabilityVoteDomain
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.RadarsCallback
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class RadarFireStoreImpl @Inject constructor(
    private val db: FirebaseFirestore
) : RadarFireStore {

    private val radarMap = mutableMapOf<String, RadarEntity>()
    private val reliabilityListeners = mutableMapOf<String, ListenerRegistration>()

    override suspend fun addRadar(radar: RadarFSData) {
        db.collection("radars")
            .add(radar)
    }

    override fun getRadarSnapshots(scope: CoroutineScope, callback: RadarsCallback) {
        val radarsListenerRegistration = db.collection("radars")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback.onError(error.message ?: "Unknown error occurred")
                    return@addSnapshotListener
                }

                snapshot?.documentChanges?.forEach { documentChange ->
                    val radarDoc = documentChange.document
                    val radarId = radarDoc.id
                    val radarData = radarDoc.toRadarDomain()

                    when (documentChange.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            addOrUpdateReliabilityListener(radarId, radarData, callback)
                        }

                        DocumentChange.Type.REMOVED -> {
                            radarMap.remove(radarId)
                            reliabilityListeners[radarId]?.remove()
                            reliabilityListeners.remove(radarId)
                            callback.onSuccess(radarMap.values.toList())
                        }
                    }
                }
            }

        // Store the radar listener for cleanup
        reliabilityListeners["radarsListener"] = radarsListenerRegistration
    }

    private fun addOrUpdateReliabilityListener(
        radarId: String,
        radarData: RadarEntity,
        callback: RadarsCallback
    ) {
        // Remove the old listener if it exists
        reliabilityListeners[radarId]?.remove()

        // Listen for changes in the reliability votes
        val reliabilityListenerRegistration = db.collection("radars")
            .document(radarId)
            .collection("reliability")
            .addSnapshotListener { reliabilitySnapshot, reliabilityError ->
                if (reliabilityError != null) {
                    callback.onError(reliabilityError.message ?: "Unknown error in reliability votes")
                    return@addSnapshotListener
                }

                if (reliabilitySnapshot != null) {
                    val reliabilityVotes = reliabilitySnapshot.documents.map { voteDoc ->
                        voteDoc.toRadarReliabilityVoteDomain()
                    }

                    // Update the radar data in the map with the new reliability votes
                    val updatedRadar = radarData.copy(reliabilityVotes = reliabilityVotes)
                    radarMap[radarId] = updatedRadar

                    // Emit the updated radar list
                    callback.onSuccess(radarMap.values.toList())
                }
            }

        // Store the listener for cleanup
        reliabilityListeners[radarId] = reliabilityListenerRegistration
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

    private fun postVote(
        voteSubcollectionRef: CollectionReference,
        radarReliabilityVote: RadarReliabilityVoteFSData
    ) {
        voteSubcollectionRef.add(
            mapOf(
                "voterUid" to radarReliabilityVote.voterUid,
                "vote" to radarReliabilityVote.vote,
                "createdAt" to radarReliabilityVote.createdAt,
                "updatedAt" to null
            )
        )
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