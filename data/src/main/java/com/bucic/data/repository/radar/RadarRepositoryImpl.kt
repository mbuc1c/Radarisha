package com.bucic.data.repository.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.repository.RadarRepository
import com.bucic.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RadarRepositoryImpl(
    private val remote: RadarDataSource.Remote
) : RadarRepository {

    override suspend fun createRadar(radar: RadarEntity) = withContext(Dispatchers.IO) {
        remote.addRadar(radar)
    }

    override suspend fun getRadars(): Result<List<RadarEntity>> = withContext(Dispatchers.IO) {
        remote.getAllRadars()
    }

    override suspend fun deleteRadar(radar: RadarEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun sync(): Boolean {
        TODO("Not yet implemented")
    }

}