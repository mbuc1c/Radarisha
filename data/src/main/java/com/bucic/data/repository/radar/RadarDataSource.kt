package com.bucic.data.repository.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.RadarsCallback
import com.bucic.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface RadarDataSource {

    interface Remote {
        suspend fun addRadar(radar: RadarEntity): Result<String>
        fun getAllRadars(callback: RadarsCallback)
        suspend fun getRadarByUid(uid: String): Result<RadarEntity>
        suspend fun deleteRadar(radar: RadarEntity): Result<String>
        suspend fun updateRadar(radar: RadarEntity): Result<String>

        suspend fun vote(radarReliabilityVote: RadarReliabilityVoteEntity): Result<String>
    }

    interface Local {
        suspend fun addRadar(radar: RadarEntity)
        suspend fun addRadars(radars: List<RadarEntity>)
        suspend fun getAllRadars(): Result<List<RadarEntity>>
    }
}