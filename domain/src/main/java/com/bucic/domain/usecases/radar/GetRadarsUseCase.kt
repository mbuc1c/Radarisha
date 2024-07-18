package com.bucic.domain.usecases.radar

import com.bucic.domain.entities.RadarEntity
import com.bucic.domain.repository.RadarRepository
import com.bucic.domain.util.Result

class GetRadarsUseCase(
    private val radarRepository: RadarRepository
) {
    suspend operator fun invoke(): Result<List<RadarEntity>> {
        return radarRepository.getRadars()
    }
}