package com.bucic.data.network.firestore.radar

import com.bucic.data.entities.radar.RadarFSData
import com.bucic.data.entities.radar.RadarReliabilityVoteFSData
import com.google.firebase.firestore.QuerySnapshot

interface RadarFireStore {
    suspend fun addRadar(radar: RadarFSData)
    suspend fun getAllRadars(): QuerySnapshot
    suspend fun deleteRadar(radarUid: String)
    suspend fun updateRadar(radar: RadarFSData, radarUid: String)
    fun vote(radarReliabilityVote: RadarReliabilityVoteFSData, radarUid: String)
}