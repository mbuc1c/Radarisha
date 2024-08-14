package com.bucic.data.network.firestore.radar

import com.bucic.data.entities.radar.RadarFSData
import com.bucic.data.entities.radar.RadarReliabilityVoteFSData
import com.bucic.domain.util.RadarsCallback
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface RadarFireStore {
    suspend fun addRadar(radar: RadarFSData)
    fun getRadarSnapshots(scope: CoroutineScope, callback: RadarsCallback)
    suspend fun getRadarByUid(radarUid: String): DocumentSnapshot
    suspend fun deleteRadar(radarUid: String)
    suspend fun updateRadar(radar: RadarFSData, radarUid: String)
    fun vote(radarReliabilityVote: RadarReliabilityVoteFSData, radarUid: String)
}