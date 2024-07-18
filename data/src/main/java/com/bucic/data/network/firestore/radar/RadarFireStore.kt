package com.bucic.data.network.firestore.radar

import com.bucic.data.entities.radar.RadarFSData
import com.google.firebase.firestore.QuerySnapshot

interface RadarFireStore {
    suspend fun addRadar(radar: RadarFSData)
    suspend fun getAllRadars(): QuerySnapshot

    //TODO: add vote, delete and update methods

}