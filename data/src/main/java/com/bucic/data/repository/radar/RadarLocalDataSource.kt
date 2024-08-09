package com.bucic.data.repository.radar

import android.util.Log
import com.bucic.data.database.radar.dao.RadarDao
import com.bucic.data.database.radar.dao.RadarReliabilityVoteDao
import com.bucic.data.entities.radar.toDomain
import com.bucic.data.mapper.toDbData
import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.util.Result

class RadarLocalDataSource(
    private val radarDao: RadarDao,
    private val radarReliabilityVoteDao: RadarReliabilityVoteDao
) : RadarDataSource.Local {
    override suspend fun addRadar(radar: RadarEntity) {
        radarDao.insert(radar.toDbData())
//        radarReliabilityVoteDao.insert(radar.reliabilityVotes.map { it.toDbData() })
    }

    override suspend fun addRadars(radars: List<RadarEntity>) {
        radarDao.deleteAll()
        radarDao.insertAll(radars.map { it.toDbData() })

        radarReliabilityVoteDao.deleteAll()
        for (radar in radars) {
            radarReliabilityVoteDao.insertAll(radar.reliabilityVotes.map { it.toDbData() })
        }
    }

    override suspend fun getAllRadars(): Result<List<RadarEntity>> = try {
        Log.d("MyTag", "Local getAllRadars: started")
        val result = radarDao.getAllRadars()
        val radarList = result.map { radarDbData ->
            val radarData = radarDbData.toDomain()
            val reliabilityVotes = radarReliabilityVoteDao.getRadarReliabilityVotesByRadarUid(radarDbData.uid)
                .map { it.toDomain() }
            radarData.copy(reliabilityVotes = reliabilityVotes)
        }
        Log.d("MyTag", "getAllRadars: $radarList")
        Result.Success(radarList)
    } catch (e: Exception) {
        Log.d("MyTag", "getAllRadars: ${e.message}")
        Result.Error(e.message.toString())
    }
}