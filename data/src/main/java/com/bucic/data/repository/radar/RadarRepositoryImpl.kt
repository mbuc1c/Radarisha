package com.bucic.data.repository.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.entities.RadarReliabilityVoteEntity
import com.bucic.domain.entities.UserEntity
import com.bucic.domain.repository.RadarRepository
import com.bucic.domain.util.RadarsCallback
import com.bucic.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class RadarRepositoryImpl(
    private val remote: RadarDataSource.Remote,
    private val local: RadarDataSource.Local
) : RadarRepository {

    override suspend fun createRadar(radar: RadarEntity): Result<String> = withContext(Dispatchers.IO) {
        remote.addRadar(radar)
    }

    override fun getRadars(callback: RadarsCallback) {
        val scope = CoroutineScope(Dispatchers.IO)
        remote.getAllRadars(scope, callback)
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
//        val radars = remote.getAllRadars()
//        return if (radars is Result.Success) {
//            local.addRadars(radars.data)
//            true
//        } else false
        TODO("Not yet implemented")
    }

    override suspend fun vote(radarReliabilityVote: RadarReliabilityVoteEntity): Result<String> = withContext(Dispatchers.IO) {
        remote.vote(radarReliabilityVote)
    }
}