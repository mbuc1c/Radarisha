package com.bucic.data.repository.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.repository.RadarRepository
import com.bucic.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RadarRepositoryImpl(
    private val remote: RadarDataSource.Remote,
    private val local: RadarDataSource.Local
) : RadarRepository {

    override suspend fun createRadar(radar: RadarEntity): Result<String> = withContext(Dispatchers.IO) {
        remote.addRadar(radar)
    }

    override suspend fun getRadars(): Result<List<RadarEntity>> = withContext(Dispatchers.IO) {
        sync()
        local.getAllRadars()
    }

    override suspend fun getRadarByUid(uid: String): Result<RadarEntity> = withContext(Dispatchers.IO) {
        remote.getRadarByUid(uid)
    }

    override suspend fun deleteRadar(radar: RadarEntity): Result<String> = withContext(Dispatchers.IO) {
        remote.deleteRadar(radar)
    }

    override suspend fun updateRadar(radar: RadarEntity): Result<String> = withContext(Dispatchers.IO) {
        remote.updateRadar(radar)
    }

    override suspend fun sync(): Boolean {
        val radars = remote.getAllRadars()
        return if (radars is Result.Success) {
            local.addRadars(radars.data)
            true
        } else false
    }

    override suspend fun vote(radarReliabilityVote: RadarReliabilityVoteEntity): Result<String> = withContext(Dispatchers.IO) {
        remote.vote(radarReliabilityVote)
    }
}