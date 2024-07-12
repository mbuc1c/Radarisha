package com.bucic.data.repository.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.Result

interface RadarDataSource {

    interface Remote {
        suspend fun addRadar(radar: RadarEntity)
        suspend fun getAllRadars(): Result<List<RadarEntity>>
    }

    interface Local {
        suspend fun addRadar(radar: RadarEntity)
        suspend fun addRadars(radars: List<RadarEntity>)
        suspend fun getAllRadars(): Result<List<RadarEntity>>
    }
}