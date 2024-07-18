package com.bucic.data.repository.radar

import android.util.Log
import com.bucic.data.exception.NoResultFoundException
import com.bucic.data.mapper.toFSData
import com.bucic.data.network.firestore.radar.RadarFireStore
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.RadarType
import com.bucic.domain.util.Result
import com.google.firebase.Timestamp

class RadarRemoteDataSource(
    private val radarFireStore: RadarFireStore
) : RadarDataSource.Remote {

    override suspend fun addRadar(radar: RadarEntity) {
        radarFireStore.addRadar(radar.toFSData())
    }

    override suspend fun getAllRadars(): Result<List<RadarEntity>> = try {
        val result = radarFireStore.getAllRadars()
        val radarList = result.documents.map {
            RadarEntity(
                uid = it.id,
                creatorUid = it.data!!["creatorUid"].toString(),
                lat = it.data!!["lat"].toString().toDouble(),
                lng = it.data!!["lng"].toString().toDouble(),
                type = RadarType.valueOf(it.data!!["type"].toString()),
                speed = it.data!!["speed"]?.toString()?.toInt(),
                createdAt = (it.data!!["createdAt"] as Timestamp).toDate(),
                updatedAt = (it.data!!["updatedAt"] as? Timestamp)?.toDate()
            )
        }
        Log.d("MyTag", "getAllRadars: $radarList")
        Result.Success(radarList)
    } catch (e: NoResultFoundException) {
        Result.Error(e.message)
    }
}