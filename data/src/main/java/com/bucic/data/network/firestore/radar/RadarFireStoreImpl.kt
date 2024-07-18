package com.bucic.data.network.firestore.radar

import com.bucic.data.entities.radar.RadarFSData
import com.bucic.data.exception.NoResultFoundException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//TODO: change rules in firebase console
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
}