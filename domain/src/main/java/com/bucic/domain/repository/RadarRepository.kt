package com.bucic.domain.repository

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.Result

interface RadarRepository {

    suspend fun createRadar(radar: RadarEntity)
    suspend fun getRadars(): Result<List<RadarEntity>>
    suspend fun deleteRadar(radar: RadarEntity)
    suspend fun sync(): Boolean

    suspend fun vote(radarReliabilityVote: RadarReliabilityVoteEntity): Result<String>

}