package com.bucic.domain.usecases.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.repository.RadarRepository
import com.bucic.domain.util.Result

class DeleteRadarUseCase(
    private val radarRepository: RadarRepository
) {
    suspend operator fun invoke(radar: RadarEntity): Result<String> {
        return radarRepository.deleteRadar(radar)
    }
}