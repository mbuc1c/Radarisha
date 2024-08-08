package com.bucic.domain.repository

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.util.Result

interface RadarRepository {

    suspend fun createRadar(radar: RadarEntity): Result<String>
    suspend fun getRadars(): Result<List<RadarEntity>>
    suspend fun getRadarByUid(uid: String): Result<RadarEntity>
    suspend fun deleteRadar(radar: RadarEntity): Result<String>
    suspend fun updateRadar(radar: RadarEntity): Result<String>
    suspend fun sync(): Boolean

    suspend fun vote(radarReliabilityVote: RadarReliabilityVoteEntity): Result<String>
}