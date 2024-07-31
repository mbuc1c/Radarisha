package com.bucic.data.network.firestore.radar

import com.bucic.data.entities.radar.RadarFSData
import com.bucic.data.entities.radar.RadarReliabilityVoteFSData
import com.google.firebase.firestore.QuerySnapshot

interface RadarFireStore {
    suspend fun addRadar(radar: RadarFSData)
    suspend fun getAllRadars(): QuerySnapshot
    fun vote(radarReliabilityVote: RadarReliabilityVoteFSData, radarUid: String)

    //TODO: add delete and update methods

}