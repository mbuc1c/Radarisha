package com.bucic.domain.repository

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.Result

interface RadarRepository {

    suspend fun createRadar(radar: RadarEntity)
    suspend fun getRadars(): Result<List<RadarEntity>> //TODO: get real-time updates
    suspend fun deleteRadar(radar: RadarEntity)
    suspend fun sync(): Boolean

}