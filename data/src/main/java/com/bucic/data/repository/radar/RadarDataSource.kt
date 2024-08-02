package com.bucic.data.repository.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.util.Result

interface RadarDataSource {

    interface Remote {
        suspend fun addRadar(radar: RadarEntity): Result<String>
        suspend fun getAllRadars(): Result<List<RadarEntity>>

        suspend fun vote(radarReliabilityVote: RadarReliabilityVoteEntity): Result<String>
    }

    interface Local {
        suspend fun addRadar(radar: RadarEntity)
        suspend fun addRadars(radars: List<RadarEntity>)
        suspend fun getAllRadars(): Result<List<RadarEntity>>
    }
}