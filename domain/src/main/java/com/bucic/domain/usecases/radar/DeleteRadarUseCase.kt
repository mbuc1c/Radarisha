package com.bucic.domain.usecases.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.repository.RadarRepository

class DeleteRadarUseCase(
    private val radarRepository: RadarRepository
) {
    suspend operator fun invoke(radar: RadarEntity) = radarRepository.deleteRadar(radar)
}